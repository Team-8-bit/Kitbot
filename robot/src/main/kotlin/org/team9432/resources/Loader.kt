package org.team9432.resources

import com.revrobotics.CANSparkBase
import com.revrobotics.CANSparkLowLevel
import com.revrobotics.CANSparkMax
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.controller.SimpleMotorFeedforward
import org.team9432.lib.RobotPeriodicManager
import org.team9432.lib.resource.Resource
import org.team9432.lib.doglog.Logger

object Loader: Resource("Loader") {
    var loaderState = State.IDLE

    val motorBottom = CANSparkMax(21, CANSparkLowLevel.MotorType.kBrushless)

    private val bottomPid = PIDController(0.0039231, 0.0, 0.0)
    private var feedforward = SimpleMotorFeedforward(0.0, 0.0086634, 0.0038234)//TODO Tune feedforward


    enum class State(val getSpeed: () -> Double) {
        LOAD({ 3750.0 }),
        DROP({ 2500.0 }),
        INTAKE({ -500.0 }),
        IDLE({ 0.0 });
    }

    init {
        motorBottom.setIdleMode(CANSparkBase.IdleMode.kCoast)
        motorBottom.setSmartCurrentLimit(60)
        motorBottom.inverted = true

        RobotPeriodicManager.startPeriodic { trackState(); log() }
    }

    private fun trackState() {
        motorBottom.setVoltage(bottomPid.calculate(loaderState.getSpeed())/2 + feedforward.calculate(bottomPid.setpoint))
    }

    private fun log() {
        Logger.log("Loader/bottomMotor/RPM", motorBottom.encoder.velocity)
        Logger.log("Loader/bottomMotor/Amps", motorBottom.outputCurrent)
        Logger.log("Loader/bottomMotor/SetSpeed", motorBottom.appliedOutput)

        Logger.log("Loader/State", loaderState)
    }

    fun setState(state: State) {
        this.loaderState = state
    }
}