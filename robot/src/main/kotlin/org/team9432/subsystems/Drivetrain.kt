package org.team9432.subsystems

import com.revrobotics.CANSparkBase
import com.revrobotics.CANSparkLowLevel
import com.revrobotics.CANSparkMax
import edu.wpi.first.wpilibj.drive.DifferentialDrive
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilderImpl
import org.littletonrobotics.junction.Logger
import org.team9432.Robot.table
import org.team9432.lib.commandbased.KSubsystem
import org.team9432.lib.commandbased.commands.InstantCommand


var driveTrain: DifferentialDrive? = null


object Drivetrain : KSubsystem() {

    private val leftTopDriveMotor = CANSparkMax(12, CANSparkLowLevel.MotorType.kBrushless)
    private val leftBottomDriveMotor = CANSparkMax(11, CANSparkLowLevel.MotorType.kBrushless)

    private val rightTopDriveMotor = CANSparkMax(13, CANSparkLowLevel.MotorType.kBrushless)
    private val rightBottomDriveMotor = CANSparkMax(14, CANSparkLowLevel.MotorType.kBrushless)

    private val drivetrainTable = table.getSubTable("Drivetrain")


    private val driveTrainBuilder = SendableBuilderImpl()


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

        driveTrain = DifferentialDrive(leftBottomDriveMotor, rightBottomDriveMotor)
        driveTrain?.isSafetyEnabled = false

        
        driveTrainBuilder.table = drivetrainTable.getSubTable("DriveTrain")
        driveTrain!!.initSendable(driveTrainBuilder)


    }

    fun tankDrive(leftSpeed: Double, rightSpeed: Double) {
        driveTrain?.tankDrive(leftSpeed, rightSpeed)
    }

    fun arcadeDrive(speed: Double, rotation: Double) {
        driveTrain?.arcadeDrive(speed, rotation, true)
    }

    override fun periodic() {
        super.periodic()
        driveTrainBuilder.update()

        Logger.recordOutput("Drivetrain/LeftBottomMotor/Amps", leftBottomDriveMotor.outputCurrent)
        Logger.recordOutput("Drivetrain/LeftBottomMotor/Volts", leftBottomDriveMotor.appliedOutput)
        Logger.recordOutput("Drivetrain/LeftBottomMotor/RPM", leftBottomDriveMotor.encoder.velocity)
        Logger.recordOutput("Drivetrain/LeftBottomMotor/SetPoint Speed", leftBottomDriveMotor.get())

        Logger.recordOutput("Drivetrain/RightBottomMotor/Amps", rightBottomDriveMotor.outputCurrent)
        Logger.recordOutput("Drivetrain/RightBottomMotor/Volts", rightBottomDriveMotor.appliedOutput)
        Logger.recordOutput("Drivetrain/RightBottomMotor/RPM", rightBottomDriveMotor.encoder.velocity)
        Logger.recordOutput("Drivetrain/RightBottomMotor/SetPoint Speed", rightBottomDriveMotor.get())

    }

    fun setIdleMode(mode: CANSparkBase.IdleMode) {
        leftTopDriveMotor.setIdleMode(mode)
        leftBottomDriveMotor.setIdleMode(mode)
        rightTopDriveMotor.setIdleMode(mode)
        rightBottomDriveMotor.setIdleMode(mode)
    }

    object Commands {
        fun arcadeDrive(speed: Double, rotation: Double) =
            InstantCommand(Drivetrain) { Drivetrain.arcadeDrive(speed, rotation) }

        fun tankDrive(leftSpeed: Double, rightSpeed: Double) =
            InstantCommand(Drivetrain) { Drivetrain.tankDrive(leftSpeed, rightSpeed) }
    }

}