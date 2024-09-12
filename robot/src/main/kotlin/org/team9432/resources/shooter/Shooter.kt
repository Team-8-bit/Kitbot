package org.team9432.resources.shooter

import org.littletonrobotics.junction.Logger
import org.team9432.lib.RobotPeriodicManager
import org.team9432.lib.resource.Resource
import org.team9432.lib.util.simSwitch


object Shooter: Resource("Shooter") {
    private val io = simSwitch(real = ShooterIONeo(), sim = ShooterIOSim())
    private val inputs = LoggedShooterIOInputs()

    private var state = State.IDLE

    var note = false

    enum class State(val getSpeed: () -> DoubleArray) {
        SHOOT({ doubleArrayOf(6500.0, 6500.0) }),
        INTAKE({ doubleArrayOf(-1700.0, -1700.0) }),
        DROP({ doubleArrayOf(500.0, 500.0) }),
        IDLE({ doubleArrayOf(0.0, 0.0) });
    }

    init {
        RobotPeriodicManager.startPeriodic { trackState(); akitUpdate() }
    }

    private fun trackState() {
        io.setSpeeds(state.getSpeed())
    }

    override fun akitUpdate() {
        io.updateInputs(inputs)
        Logger.processInputs("Shooter", inputs)
    }

    fun setState(state: State) {
        Shooter.state = state
        trackState()
        Logger.recordOutput("Loader/State", state)
    }

    fun isIntaking(): Boolean { return state == State.INTAKE }
}