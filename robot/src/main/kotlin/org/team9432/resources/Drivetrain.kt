package org.team9432.resources

import com.revrobotics.CANSparkBase
import com.revrobotics.CANSparkLowLevel
import com.revrobotics.CANSparkMax
import edu.wpi.first.wpilibj.drive.DifferentialDrive
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import org.team9432.lib.RobotPeriodicManager
import org.team9432.lib.doglog.Logger
import org.team9432.lib.resource.Resource


var driveTrain: DifferentialDrive? = null


object Drivetrain : Resource("Drivetrain"){

    private val leftTopDriveMotor = CANSparkMax(12, CANSparkLowLevel.MotorType.kBrushless)
    private val leftBottomDriveMotor = CANSparkMax(11, CANSparkLowLevel.MotorType.kBrushless)

    private val rightTopDriveMotor = CANSparkMax(13, CANSparkLowLevel.MotorType.kBrushless)
    private val rightBottomDriveMotor = CANSparkMax(14, CANSparkLowLevel.MotorType.kBrushless)



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

        leftTopDriveMotor.enableVoltageCompensation(12.0)
        rightTopDriveMotor.enableVoltageCompensation(12.0)
        rightBottomDriveMotor.enableVoltageCompensation(12.0)
        leftBottomDriveMotor.enableVoltageCompensation(12.0)

        driveTrain = DifferentialDrive(leftBottomDriveMotor, rightBottomDriveMotor)
        driveTrain?.isSafetyEnabled = false

        RobotPeriodicManager.startPeriodic { log(); }

        SmartDashboard.putData(driveTrain)
    }

    fun tankDrive(leftSpeed: Double, rightSpeed: Double) {
        driveTrain?.tankDrive(leftSpeed, rightSpeed)
    }

    fun arcadeDrive(speed: Double, rotation: Double) {
        driveTrain?.arcadeDrive(speed, rotation, true)
    }


    fun log() {
        Logger.log("Drivetrain/LeftBottomMotor/Amps", leftBottomDriveMotor.outputCurrent)
        Logger.log("Drivetrain/LeftBottomMotor/Volts", leftBottomDriveMotor.appliedOutput)
        Logger.log("Drivetrain/LeftBottomMotor/RPM", leftBottomDriveMotor.encoder.velocity)
        Logger.log("Drivetrain/LeftBottomMotor/SetPoint Speed", leftBottomDriveMotor.get())

        Logger.log("Drivetrain/RightBottomMotor/Amps", rightBottomDriveMotor.outputCurrent)
        Logger.log("Drivetrain/RightBottomMotor/Volts", rightBottomDriveMotor.appliedOutput)
        Logger.log("Drivetrain/RightBottomMotor/RPM", rightBottomDriveMotor.encoder.velocity)
        Logger.log("Drivetrain/RightBottomMotor/SetPoint Speed", rightBottomDriveMotor.get())

        Logger.log("Drivetrain/LeftTopMotor/Amps", leftTopDriveMotor.outputCurrent)
        Logger.log("Drivetrain/LeftTopMotor/Volts", leftTopDriveMotor.appliedOutput)
        Logger.log("Drivetrain/LeftTopMotor/RPM", leftTopDriveMotor.encoder.velocity)
        Logger.log("Drivetrain/LeftTopMotor/SetPoint Speed", leftTopDriveMotor.get())

        Logger.log("Drivetrain/RightTopMotor/Amps", rightTopDriveMotor.outputCurrent)
        Logger.log("Drivetrain/RightTopMotor/Volts", rightTopDriveMotor.appliedOutput)
        Logger.log("Drivetrain/RightTopMotor/RPM", rightTopDriveMotor.encoder.velocity)
        Logger.log("Drivetrain/RightTopMotor/SetPoint Speed", rightTopDriveMotor.get())
    }

    fun setIdleMode(mode: CANSparkBase.IdleMode) {
        leftTopDriveMotor.setIdleMode(mode)
        leftBottomDriveMotor.setIdleMode(mode)
        rightTopDriveMotor.setIdleMode(mode)
        rightBottomDriveMotor.setIdleMode(mode)
    }




}