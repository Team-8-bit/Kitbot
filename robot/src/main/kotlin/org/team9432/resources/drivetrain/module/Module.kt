package org.team9432.resources.drivetrain.module

import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.controller.SimpleMotorFeedforward
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.kinematics.SwerveModulePosition
import edu.wpi.first.math.kinematics.SwerveModuleState
import edu.wpi.first.math.util.Units
import org.littletonrobotics.junction.Logger
import org.team9432.Robot
import org.team9432.lib.coroutines.Team8BitRobot.Runtime.*
import kotlin.math.cos

class Module(private val io: ModuleIO, private val index: Int) {
    private val inputs: LoggedModuleIOInputs = LoggedModuleIOInputs()

    private var driveFeedforward: SimpleMotorFeedforward
    private var driveFeedback: PIDController
    private var turnFeedback: PIDController
    private var angleSetpoint: Rotation2d? = null // Setpoint for closed loop control, null for open loop
    private var speedSetpoint: Double? = null // Setpoint for closed loop control, null for open loop
    private var turnRelativeOffset: Rotation2d? = null // Relative + Offset = Absolute

    init {
        when (Robot.runtime) {
            REAL, REPLAY -> {
                driveFeedforward = SimpleMotorFeedforward(0.1, 0.13)
                driveFeedback = PIDController(0.05, 0.0, 0.0)
                turnFeedback = PIDController(7.0, 0.0, 0.0)
            }

            SIM -> {
                driveFeedforward = SimpleMotorFeedforward(0.0, 0.13)
                driveFeedback = PIDController(0.1, 0.0, 0.0)
                turnFeedback = PIDController(10.0, 0.0, 0.0)
            }
        }

        turnFeedback.enableContinuousInput(-Math.PI, Math.PI)
    }

    fun periodic() {
        io.updateInputs(inputs)
        Logger.processInputs("Drive/Module$index", inputs)

        // On first cycle, reset relative turn encoder
        // Wait until absolute angle is nonzero in case it wasn't initialized yet
        if (turnRelativeOffset == null && inputs.turnAbsolutePosition.radians != 0.0) {
            turnRelativeOffset = inputs.turnAbsolutePosition.minus(inputs.turnPosition)
        }

        // Run closed loop turn control
        if (angleSetpoint != null) {
            io.setTurnVoltage(
                turnFeedback.calculate(angle.radians, angleSetpoint!!.radians)
            )

            // Run closed loop drive control
            // Only allowed if closed loop turn control is running
            if (speedSetpoint != null) {
                // Scale velocity based on turn error
                //
                // When the error is 90°, the velocity setpoint should be 0. As the wheel turns
                // towards the setpoint, its velocity should increase. This is achieved by
                // taking the component of the velocity in the direction of the setpoint.
                val adjustSpeedSetpoint = speedSetpoint!! * cos(turnFeedback.positionError)

                // Run drive controller
                val velocityRadPerSec = adjustSpeedSetpoint / WHEEL_RADIUS
                io.setDriveVoltage(
                    driveFeedforward.calculate(velocityRadPerSec)
                            + driveFeedback.calculate(inputs.driveVelocityRadPerSec, velocityRadPerSec)
                )
            }
        }
    }

    /** Runs the module with the specified setpoint state. Returns the optimized state.  */
    fun runSetpoint(state: SwerveModuleState): SwerveModuleState {
        // Optimize state based on current angle
        // Controllers run in "periodic" when the setpoint is not null
        val optimizedState = SwerveModuleState.optimize(state, angle)

        // Update setpoints, controllers run in "periodic"
        angleSetpoint = optimizedState.angle
        speedSetpoint = optimizedState.speedMetersPerSecond

        return optimizedState
    }

    /** Runs the module with the specified voltage while controlling to zero degrees.  */
    fun runCharacterization(volts: Double) {
        // Closed loop turn control
        angleSetpoint = Rotation2d()

        // Open loop drive control
        io.setDriveVoltage(volts)
        speedSetpoint = null
    }

    /** Disables all outputs to motors.  */
    fun stop() {
        io.setTurnVoltage(0.0)
        io.setDriveVoltage(0.0)

        // Disable closed loop control for turn and drive
        angleSetpoint = null
        speedSetpoint = null
    }

    private val angle: Rotation2d
        /** Returns the current turn angle of the module.  */
        get() = if (turnRelativeOffset == null) {
            Rotation2d()
        } else {
            inputs.turnPosition.plus(turnRelativeOffset)
        }

    val positionMeters: Double
        /** Returns the current drive position of the module in meters.  */
        get() = inputs.drivePositionRad * WHEEL_RADIUS

    val velocityMetersPerSec: Double
        /** Returns the current drive velocity of the module in meters per second.  */
        get() = inputs.driveVelocityRadPerSec * WHEEL_RADIUS

    val position: SwerveModulePosition
        /** Returns the module position (turn angle and drive position).  */
        get() = SwerveModulePosition(positionMeters, angle)

    val state: SwerveModuleState
        /** Returns the module state (turn angle and drive velocity).  */
        get() = SwerveModuleState(velocityMetersPerSec, angle)

    val characterizationVelocity: Double
        /** Returns the drive velocity in radians/sec.  */
        get() = inputs.driveVelocityRadPerSec

    companion object {
        private val WHEEL_RADIUS = Units.inchesToMeters(2.0)
    }
}