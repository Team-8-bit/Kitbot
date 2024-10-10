package org.team9432.resources.drivetrain.module

import com.ctre.phoenix6.BaseStatusSignal
import com.ctre.phoenix6.StatusSignal
import com.ctre.phoenix6.configs.CANcoderConfiguration
import com.ctre.phoenix6.hardware.CANcoder
import com.revrobotics.CANSparkBase.IdleMode
import com.revrobotics.CANSparkLowLevel
import com.revrobotics.CANSparkMax
import com.revrobotics.RelativeEncoder
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.util.Units
import org.team9432.lib.constants.SwerveConstants
import org.team9432.resources.drivetrain.module.ModuleIO.Module
import org.team9432.resources.drivetrain.module.ModuleIO.ModuleIOInputs

/**
 * Module IO implementation for SparkMax drive motor controller, SparkMax turn motor controller (NEO
 * or NEO 550), and analog absolute encoder connected to the RIO
 *
 *
 * NOTE: This implementation should be used as a starting point and adapted to different hardware
 * configurations (e.g. If using a CANcoder, copy from "ModuleIOTalonFX")
 *
 *
 * To calibrate the absolute encoder offsets, point the modules straight (such that forward
 * motion on the drive motor will propel the robot forward) and copy the reported values from the
 * absolute encoders using AdvantageScope. These values are logged under
 * "/Drive/ModuleX/TurnAbsolutePositionRad"
 */
class ModuleIONeo(module: Module): ModuleIO {
    private val driveSparkMax: CANSparkMax
    private val turnSparkMax: CANSparkMax

    private val driveEncoder: RelativeEncoder
    private val turnEncoder: RelativeEncoder
    private val cancoder: CANcoder

    private val turnAbsolutePosition: StatusSignal<Double>

    private val isTurnMotorInverted = true
    private var absoluteEncoderOffset: Rotation2d

    init {
        when (module) {
            Module.FRONT_LEFT -> {
                driveSparkMax = CANSparkMax(1, CANSparkLowLevel.MotorType.kBrushless)
                turnSparkMax = CANSparkMax(2, CANSparkLowLevel.MotorType.kBrushless)
                cancoder = CANcoder(1)
                absoluteEncoderOffset = Rotation2d.fromDegrees(128.320) // FL
            }

            Module.FRONT_RIGHT -> {
                driveSparkMax = CANSparkMax(3, CANSparkLowLevel.MotorType.kBrushless)
                turnSparkMax = CANSparkMax(4, CANSparkLowLevel.MotorType.kBrushless)
                cancoder = CANcoder(2)
                absoluteEncoderOffset = Rotation2d.fromDegrees(-82.090) // FR

            }

            Module.BACK_LEFT -> {
                driveSparkMax = CANSparkMax(5, CANSparkLowLevel.MotorType.kBrushless)
                turnSparkMax = CANSparkMax(6, CANSparkLowLevel.MotorType.kBrushless)
                cancoder = CANcoder(3)
                absoluteEncoderOffset = Rotation2d.fromDegrees(70.225) // BL
            }

            Module.BACK_RIGHT -> {
                driveSparkMax = CANSparkMax(7, CANSparkLowLevel.MotorType.kBrushless)
                turnSparkMax = CANSparkMax(8, CANSparkLowLevel.MotorType.kBrushless)
                cancoder = CANcoder(4)
                absoluteEncoderOffset = Rotation2d.fromDegrees(18.193)// BR

            }
        }

        // Only uncomment these if you just replaced a spark max, otherwise there's a chance it resets but doesn't persist other settings
//        driveSparkMax.restoreFactoryDefaults()
//        turnSparkMax.restoreFactoryDefaults()

        driveSparkMax.setCANTimeout(250)
        turnSparkMax.setCANTimeout(250)

        driveEncoder = driveSparkMax.encoder
        turnEncoder = turnSparkMax.encoder

        turnSparkMax.inverted = isTurnMotorInverted
        driveSparkMax.setSmartCurrentLimit(40)
        turnSparkMax.setSmartCurrentLimit(30)
        driveSparkMax.enableVoltageCompensation(12.0)
        turnSparkMax.enableVoltageCompensation(12.0)

        driveEncoder.setPosition(0.0)
        driveEncoder.setMeasurementPeriod(10)
        driveEncoder.setAverageDepth(2)

        turnEncoder.setPosition(0.0)
        turnEncoder.setMeasurementPeriod(10)
        turnEncoder.setAverageDepth(2)

        driveSparkMax.setCANTimeout(0)
        turnSparkMax.setCANTimeout(0)

        driveSparkMax.setIdleMode(IdleMode.kBrake)
        turnSparkMax.setIdleMode(IdleMode.kBrake)

        driveSparkMax.burnFlash()
        turnSparkMax.burnFlash()

        val cancoderConfig = CANcoderConfiguration()
        cancoder.configurator.apply(cancoderConfig)

        turnAbsolutePosition = cancoder.absolutePosition
        BaseStatusSignal.setUpdateFrequencyForAll(50.0, turnAbsolutePosition)
        cancoder.optimizeBusUtilization()
    }

    override fun updateInputs(inputs: ModuleIOInputs) {
        BaseStatusSignal.refreshAll(turnAbsolutePosition)

        inputs.drivePositionRad = Units.rotationsToRadians(driveEncoder.position) / DRIVE_GEAR_RATIO
        inputs.driveVelocityRadPerSec = Units.rotationsPerMinuteToRadiansPerSecond(driveEncoder.velocity) / DRIVE_GEAR_RATIO
        inputs.driveAppliedVolts = driveSparkMax.appliedOutput * driveSparkMax.busVoltage
        inputs.driveCurrentAmps = driveSparkMax.outputCurrent

        inputs.turnAbsolutePosition = Rotation2d.fromRotations(turnAbsolutePosition.valueAsDouble).minus(absoluteEncoderOffset)
        inputs.turnPosition = Rotation2d.fromRotations(turnEncoder.position / TURN_GEAR_RATIO)
        inputs.turnVelocityRadPerSec = (Units.rotationsPerMinuteToRadiansPerSecond(turnEncoder.velocity) / TURN_GEAR_RATIO)
        inputs.turnAppliedVolts = turnSparkMax.appliedOutput * turnSparkMax.busVoltage
        inputs.turnCurrentAmps = turnSparkMax.outputCurrent
    }

    override fun setDriveVoltage(volts: Double) {
        driveSparkMax.setVoltage(volts)
    }

    override fun setTurnVoltage(volts: Double) {
        turnSparkMax.setVoltage(volts)
    }

    companion object {
        private const val DRIVE_GEAR_RATIO = SwerveConstants.MK4I_L3_DRIVE_REDUCTION
        private const val TURN_GEAR_RATIO = 150.0 / 7.0
    }
}