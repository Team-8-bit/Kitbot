package org.team9432.subsystems

import com.revrobotics.CANSparkBase
import com.revrobotics.CANSparkLowLevel
import com.revrobotics.CANSparkMax
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.controller.SimpleMotorFeedforward
import org.littletonrobotics.junction.Logger
import org.team9432.lib.commandbased.KSubsystem
import org.team9432.lib.commandbased.commands.InstantCommand

object Shooter : KSubsystem() {
    var note = false
    val motorBottom = CANSparkMax(21, CANSparkLowLevel.MotorType.kBrushless)
    val motorTop = CANSparkMax(22, CANSparkLowLevel.MotorType.kBrushless)
    val motorSide = CANSparkMax(23, CANSparkLowLevel.MotorType.kBrushless)
    private val topPid = PIDController(0.0039231, 0.0, 0.0)
    private val bottomPid = PIDController(0.0039231, 0.0, 0.0)
    private val sidePid = PIDController(0.0039231, 0.0, 0.0)
    private var feedforward = SimpleMotorFeedforward(0.0, 0.0086634, 0.0038234)//TODO Tune feedforward

    init {
        motorBottom.setIdleMode(CANSparkBase.IdleMode.kCoast)
        motorBottom.setSmartCurrentLimit(60)
        motorBottom.inverted = true
        motorTop.setIdleMode(CANSparkBase.IdleMode.kCoast)
        motorTop.setSmartCurrentLimit(60)
        motorTop.inverted = true
        motorSide.setIdleMode(CANSparkBase.IdleMode.kCoast)
        motorSide.setSmartCurrentLimit(60)
        motorSide.inverted = false
    }


    override fun periodic() {
        Logger.recordOutput("Shooter/bottomMotor/RPM", motorBottom.encoder.velocity)
        Logger.recordOutput("Shooter/bottomMotor/SetPoint Speed", bottomPid.setpoint)
        Logger.recordOutput("Shooter/bottomMotor/Amps", motorBottom.outputCurrent)
        Logger.recordOutput("Shooter/bottomMotor/Volts", motorBottom.appliedOutput)

        Logger.recordOutput("Shooter/topMotor/RPM", motorTop.encoder.velocity)
        Logger.recordOutput("Shooter/topMotor/SetPoint Speed", topPid.setpoint)
        Logger.recordOutput("Shooter/topMotor/Amps", motorTop.outputCurrent)
        Logger.recordOutput("Shooter/topMotor/Volts", motorTop.appliedOutput)

        Logger.recordOutput("Shooter/sideMotor/RPM", motorSide.encoder.velocity)
        Logger.recordOutput("Shooter/sideMotor/SetPoint Speed", sidePid.setpoint)
        Logger.recordOutput("Shooter/sideMotor/Amps", motorSide.outputCurrent)
        Logger.recordOutput("Shooter/sideMotor/Volts", motorSide.appliedOutput)

        Logger.recordOutput("Shooter/NoteInRobot", note)


    }

    fun setBottomSpeed(speed: Double) {
        motorBottom.setVoltage((bottomPid.calculate(speed) /2) + feedforward.calculate(bottomPid.setpoint))
    }

    fun setTopSpeed(speed: Double) {
        motorTop.setVoltage((topPid.calculate(speed) /2) + feedforward.calculate(topPid.setpoint))
    }

    fun setSideSpeed(speed: Double) {
        motorSide.setVoltage((sidePid.calculate(speed) /2) + feedforward.calculate(sidePid.setpoint))
    }

    object Commands {
        fun setNoteInRobot(boolean: Boolean) = InstantCommand(Shooter) { note = boolean }
        fun setTopSpeed(speed: Double) = InstantCommand(Shooter) { Shooter.setTopSpeed(speed) }
        fun setBottomSpeed(speed: Double) = InstantCommand(Shooter) { Shooter.setBottomSpeed(speed) }
        fun setSideSpeed(speed: Double) = InstantCommand(Shooter) { Shooter.setSideSpeed(speed) }
    }

}