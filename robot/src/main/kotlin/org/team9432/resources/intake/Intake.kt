package org.team9432.resources.intake

import org.littletonrobotics.junction.Logger
import org.team9432.lib.Beambreak
import org.team9432.lib.RobotPeriodicManager
import org.team9432.lib.resource.Resource
import org.team9432.lib.util.simSwitch


object Intake: Resource("Intake") {
    private val io = simSwitch(real = IntakeIONeo(), sim = IntakeIOSim())
    private val inputs = LoggedIntakeIOInputs()

    private var state = State.IDLE

    val beambreak = Beambreak(9)

    enum class State(val getVoltage: () -> DoubleArray) {
        INTAKE({ doubleArrayOf(7.0, 10.0) }),
        LOAD({ doubleArrayOf(6.0, 6.0) }),
        REVERSE({ doubleArrayOf(-4.0, 0.0) }),
        IDLE({ doubleArrayOf(0.0, 0.0) });
    }

    init {
        RobotPeriodicManager.startPeriodic { trackState(); akitUpdate() }
    }

    private fun trackState() {
        io.setVoltage(state.getVoltage())
    }

    override fun akitUpdate() {
        io.updateInputs(inputs)
        Logger.processInputs("Intake", inputs)
        Logger.recordOutput("Intake/BeamBreak",beambreak.isTripped())
    }

    fun setState(state: State) {
        Intake.state = state
        trackState()
        Logger.recordOutput("Intake/State", state)
    }

    fun isIntaking(): Boolean { return state == State.INTAKE }
}