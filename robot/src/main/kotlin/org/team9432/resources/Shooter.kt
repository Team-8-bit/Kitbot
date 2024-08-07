package org.team9432.resources

import com.revrobotics.CANSparkBase
import com.revrobotics.CANSparkLowLevel
import com.revrobotics.CANSparkMax
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.controller.SimpleMotorFeedforward
import org.team9432.lib.coroutines.CoroutineRobot
import org.team9432.lib.doglog.Logger
import org.team9432.lib.resource.Resource

object Shooter : Resource("Shooter") {
    private var state = State.IDLE

    var note = false

    val motorTop = CANSparkMax(22, CANSparkLowLevel.MotorType.kBrushless)
    val motorSide = CANSparkMax(23, CANSparkLowLevel.MotorType.kBrushless)

    private val topPid = PIDController(0.0039231, 0.0, 0.0)
    private val sidePid = PIDController(0.0039231, 0.0, 0.0)
    private var feedforward = SimpleMotorFeedforward(0.0, 0.0086634, 0.0038234)//TODO Tune feedforward

    enum class State(val getSpeed: () -> Double) {
        SHOOT({ 5000.0 }),
        INTAKE({ -5000.0 }),
        IDLE({ 0.0 });
    }

    init {

        motorTop.setIdleMode(CANSparkBase.IdleMode.kCoast)
        motorTop.setSmartCurrentLimit(60)
        motorTop.inverted = true

        motorSide.setIdleMode(CANSparkBase.IdleMode.kCoast)
        motorSide.setSmartCurrentLimit(60)
        motorSide.inverted = false

        CoroutineRobot.startPeriodic { trackState(); log() }
    }

    private fun trackState() {
        motorTop.setVoltage(topPid.calculate(state.getSpeed()) + feedforward.calculate(topPid.setpoint))
        motorSide.setVoltage(sidePid.calculate(state.getSpeed()) + feedforward.calculate(sidePid.setpoint))
    }

    fun log() {

        Logger.log("Shooter/sideMotor/RPM", motorSide.encoder.velocity)
        Logger.log("Shooter/sideMotor/SetPoint Speed", sidePid.setpoint)
        Logger.log("Shooter/sideMotor/Amps", motorSide.outputCurrent)
        Logger.log("Shooter/sideMotor/Volts", motorSide.appliedOutput)

        Logger.log("Shooter/topMotor/RPM", motorTop.encoder.velocity)
        Logger.log("Shooter/topMotor/SetPoint Speed", topPid.setpoint)
        Logger.log("Shooter/topMotor/Amps", motorTop.outputCurrent)
        Logger.log("Shooter/topMotor/Volts", motorTop.appliedOutput)

        Logger.log("Shooter/NoteInRobot", note)

        Logger.log("Shooter/State", state)
    }

    fun setState(state: State) {
        this.state = state
    }

}