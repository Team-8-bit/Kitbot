package org.team9432.oi


import edu.wpi.first.math.MathUtil
import edu.wpi.first.math.filter.SlewRateLimiter
import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.geometry.Transform2d
import edu.wpi.first.math.geometry.Translation2d
import edu.wpi.first.math.kinematics.ChassisSpeeds
import org.team9432.Actions
import org.team9432.Robot
import org.team9432.RobotController
import org.team9432.lib.RobotPeriodicManager
import org.team9432.lib.input.XboxController
import org.team9432.resources.drivetrain.Drivetrain
import kotlin.math.hypot
import kotlin.math.pow

object Controls {
    val controller = XboxController(0)

    private val ratelimitX = SlewRateLimiter(20.0)
    private val ratelimitY = SlewRateLimiter(20.0)

    init {
        controller.a
            .onTrue { Actions.drop() }

//        controller.b
//            .onTrue { RobotController.setAction { Actions.intake() } }

        controller.y
            .onTrue { RobotController.resetRequests(); RobotController.setAction { Actions.idle() } }

        controller.rightBumper
            .onTrue { RobotController.setAction { Actions.startShooting() } }
            .onFalse { RobotController.setAction { Actions.stopShooting() } }


        controller.x
            .onTrue { Drivetrain.resetGyro() }

        controller.b
            .onTrue { Actions.outTake() }

        controller.leftBumper
            .onTrue { RobotController.setAction { Actions.groundIntake() } }

        RobotPeriodicManager.startPeriodic {
            if (Robot.mode.isTeleop) {

                val translationalVelocity = getTranslationalSpeed()
                Drivetrain.runFieldRelative(
                    ChassisSpeeds(
                        ratelimitX.calculate(translationalVelocity.x * Drivetrain.MAX_VELOCITY_MPS),
                        ratelimitY.calculate(translationalVelocity.y * Drivetrain.MAX_VELOCITY_MPS),
                        getRotationalSpeed() * Drivetrain.MAX_ANG_VELOCITY_RAD_PER_SEC
                    )
                )
            }
        }
    }

    private fun getRotationalSpeed(): Double {
        return getJoystickRotationSpeed()
    }

    private fun getJoystickRotationSpeed() = -controller.rightX

    private fun getTriggerRotationSpeed(): Double {
        val rightAxis = controller.rightTriggerAxis
        val leftAxis = controller.leftTriggerAxis
        return ((rightAxis.pow(2) * -1) + leftAxis.pow(2))
    }

    // https://github.com/Mechanical-Advantage/RobotCode2024/blob/a025615a52193b7709db7cf14c51c57be17826f2/src/main/java/org/littletonrobotics/frc2024/subsystems/drive/controllers/TeleopDriveController.java#L83
    private fun getTranslationalSpeed(): Translation2d {
        val x: Double = -controller.leftYRaw
        val y: Double = -controller.leftXRaw

        val deadband = 0.15

        // Apply deadband
        var linearMagnitude = MathUtil.applyDeadband(hypot(x, y), deadband)
        val linearDirection = Rotation2d(x, y)

        // Square magnitude
        linearMagnitude = linearMagnitude * linearMagnitude

        // Calcaulate new linear velocity
        val linearVelocity =
            Pose2d(Translation2d(), linearDirection)
                .transformBy(Transform2d(linearMagnitude, 0.0, Rotation2d()))
                .translation

        return linearVelocity
    }
}