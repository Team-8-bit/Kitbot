package org.team9432.resources.loader

import org.littletonrobotics.junction.Logger
import org.team9432.lib.RobotPeriodicManager
import org.team9432.lib.resource.Resource
import org.team9432.lib.util.simSwitch

object Loader: Resource("Loader") {
    private val io = simSwitch(real = LoaderIONeo(), sim = LoaderIOSim())
    private val inputs = LoggedLoaderIOInputs()

    private var state = State.IDLE
//    val motorBottom = CANSparkMax(21, CANSparkLowLevel.MotorType.kBrushless)
//    private val bottomPid = PIDController(0.0039231, 0.0, 0.0)
//    private var feedforward = SimpleMotorFeedforward(0.0, 0.0086634, 0.0038234)//TODO Tune feedforward


    enum class State(val getSpeed: () -> Double) {
        LOAD({ 3750.0 }),
        SPEED({ 6500.0 }),
        DROP({ 2500.0 }),
        INTAKE({ -500.0 }),
        IDLE({ 0.0 });
    }

    init {
        RobotPeriodicManager.startPeriodic { trackState(); akitUpdate() }
    }

    private fun trackState() {
        io.setSpeed(state.getSpeed())
    }

    override fun akitUpdate() {
        io.updateInputs(inputs)
        Logger.processInputs("Loader", inputs)
    }

    fun setState(state: State) {
        Loader.state = state
        trackState()
        Logger.recordOutput("Loader/State", state)
    }

}