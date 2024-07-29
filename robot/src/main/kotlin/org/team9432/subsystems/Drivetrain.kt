package org.team9432.subsystems

import com.revrobotics.CANSparkBase
import com.revrobotics.CANSparkLowLevel
import com.revrobotics.CANSparkMax
import edu.wpi.first.math.MathUtil
import edu.wpi.first.wpilibj.drive.DifferentialDrive
import org.team9432.Robot.table
import org.team9432.lib.commandbased.KSubsystem
import org.team9432.lib.commandbased.commands.InstantCommand
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.withSign


object Drivetrain : KSubsystem() {

    private var leftSpeed = 0.0
    private var rightSpeed = 0.0

    private val leftTopDriveMotor = CANSparkMax(12, CANSparkLowLevel.MotorType.kBrushless)
    private val leftBottomDriveMotor = CANSparkMax(11, CANSparkLowLevel.MotorType.kBrushless)

    private val rightTopDriveMotor = CANSparkMax(13, CANSparkLowLevel.MotorType.kBrushless)
    private val rightBottomDriveMotor = CANSparkMax(14, CANSparkLowLevel.MotorType.kBrushless)

    private val drivetrainTable = table.getSubTable("Drivetrain")

    private val drivetrainTypePublisher = drivetrainTable.getStringTopic(".type").publish()
    private val leftBottomMotorSpeedPublisher = drivetrainTable.getDoubleTopic("Left Motor Speed").publish()
    private val rightBottomMotorSpeedPublisher = drivetrainTable.getDoubleTopic("Right Motor Speed").publish()


    init {
        leftTopDriveMotor.follow(leftBottomDriveMotor)
        rightTopDriveMotor.follow(rightBottomDriveMotor)

        rightBottomDriveMotor.inverted = false
        leftBottomDriveMotor.inverted = true

        leftTopDriveMotor.setIdleMode(CANSparkBase.IdleMode.kBrake)
        rightTopDriveMotor.setIdleMode(CANSparkBase.IdleMode.kBrake)
        rightBottomDriveMotor.setIdleMode(CANSparkBase.IdleMode.kBrake)
        leftBottomDriveMotor.setIdleMode(CANSparkBase.IdleMode.kBrake)

        leftTopDriveMotor.setSmartCurrentLimit(60)
        rightTopDriveMotor.setSmartCurrentLimit(60)
        rightBottomDriveMotor.setSmartCurrentLimit(60)
        leftBottomDriveMotor.setSmartCurrentLimit(60)


    }

    fun tankDrive(leftSpeed: Double, rightSpeed: Double) {
        val speeds = tankDriveIK(leftSpeed, rightSpeed, false)
        leftBottomDriveMotor.set(speeds.left)
        rightBottomDriveMotor.set(speeds.right)
        Drivetrain.leftSpeed = speeds.left
        Drivetrain.rightSpeed = speeds.right
    }

    fun arcadeDrive(speed: Double, rotation: Double) {
        val speeds = arcadeDriveIK(speed, rotation, true)
        leftBottomDriveMotor.set(speeds.left)
        rightBottomDriveMotor.set(speeds.right)
        leftSpeed = speeds.left
        rightSpeed = speeds.right
    }

    override fun periodic() {
        super.periodic()
        drivetrainTypePublisher.set("DifferentialDrive")
        leftBottomMotorSpeedPublisher.set(leftSpeed)
        rightBottomMotorSpeedPublisher.set(rightSpeed)


    }

    fun arcadeDriveIK(xSpeed: Double, zRotation: Double, squareInputs: Boolean): DifferentialDrive.WheelSpeeds {
        var xSpeed = xSpeed
        var zRotation = zRotation
        xSpeed = MathUtil.clamp(xSpeed, -1.0, 1.0)
        zRotation = MathUtil.clamp(zRotation, -1.0, 1.0)
        if (squareInputs) {
            xSpeed = (xSpeed * xSpeed).withSign(xSpeed)
            zRotation = (zRotation * zRotation).withSign(zRotation)
        }

        var leftSpeed = xSpeed - zRotation
        var rightSpeed = xSpeed + zRotation
        val greaterInput = max(abs(xSpeed), abs(zRotation))
        val lesserInput = min(abs(xSpeed), abs(zRotation))
        if (greaterInput == 0.0) {
            return DifferentialDrive.WheelSpeeds(0.0, 0.0)
        } else {
            val saturatedInput = (greaterInput + lesserInput) / greaterInput
            leftSpeed /= saturatedInput
            rightSpeed /= saturatedInput
            return DifferentialDrive.WheelSpeeds(leftSpeed, rightSpeed)
        }
    }

    fun tankDriveIK(leftSpeed: Double, rightSpeed: Double, squareInputs: Boolean): DifferentialDrive.WheelSpeeds {
        var leftSpeed = leftSpeed
        var rightSpeed = rightSpeed
        leftSpeed = MathUtil.clamp(leftSpeed, -1.0, 1.0)
        rightSpeed = MathUtil.clamp(rightSpeed, -1.0, 1.0)
        if (squareInputs) {
            leftSpeed = (leftSpeed * leftSpeed).withSign(leftSpeed)
            rightSpeed = (rightSpeed * rightSpeed).withSign(rightSpeed)
        }

        return DifferentialDrive.WheelSpeeds(leftSpeed, rightSpeed)
    }

    object Commands {
        fun arcadeDrive(speed: Double, rotation: Double) =
            InstantCommand(Drivetrain) { Drivetrain.arcadeDrive(speed, rotation) }

        fun tankDrive(leftSpeed: Double, rightSpeed: Double) =
            InstantCommand(Drivetrain) { Drivetrain.tankDrive(leftSpeed, rightSpeed) }
    }

}