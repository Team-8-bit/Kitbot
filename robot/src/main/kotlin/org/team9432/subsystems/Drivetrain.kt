package org.team9432.subsystems

import com.revrobotics.CANSparkBase
import com.revrobotics.CANSparkLowLevel
import com.revrobotics.CANSparkMax
import edu.wpi.first.wpilibj.drive.DifferentialDrive
import org.team9432.Controls
import org.team9432.lib.commandbased.KCommandScheduler
import org.team9432.lib.commandbased.KSubsystem
import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.commandbased.commands.SimpleCommand


var driveTrain: DifferentialDrive? = null

object Drivetrain: KSubsystem() {
    private val leftTopDriveMotor = CANSparkMax(12, CANSparkLowLevel.MotorType.kBrushless)
    private val leftBottomDriveMotor = CANSparkMax(11, CANSparkLowLevel.MotorType.kBrushless)

    private val rightTopDriveMotor = CANSparkMax(13, CANSparkLowLevel.MotorType.kBrushless)
    private val rightBottomDriveMotor = CANSparkMax(14, CANSparkLowLevel.MotorType.kBrushless)

    var slow = false

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
    }

    fun tankDrive(leftSpeed: Double, rightSpeed: Double){
        driveTrain?.tankDrive(leftSpeed, rightSpeed)
    }
    fun arcadeDrive(speed: Double, rotation: Double){
        if(slow){
            driveTrain?.arcadeDrive(speed*0.5, rotation*0.5)
        }else{
            driveTrain?.arcadeDrive(speed, rotation)
        }

    }

    object Commands {
        fun arcadeDrive(speed: Double, rotation: Double) = InstantCommand(Drivetrain) { Drivetrain.arcadeDrive(speed,rotation) }
        fun tankDrive(leftSpeed: Double, rightSpeed: Double) = InstantCommand(Drivetrain) { Drivetrain.tankDrive(leftSpeed,rightSpeed) }
    }

}