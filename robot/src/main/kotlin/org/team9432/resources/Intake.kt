package org.team9432.resources

import com.revrobotics.CANSparkBase
import com.revrobotics.CANSparkLowLevel
import com.revrobotics.CANSparkMax
import org.team9432.lib.Beambreak
import org.team9432.lib.RobotPeriodicManager
import org.team9432.lib.resource.Resource
import org.team9432.lib.doglog.Logger

object Intake: Resource("Intake") {
    var intakeState = State.IDLE

    private val motorBack = CANSparkMax(31, CANSparkLowLevel.MotorType.kBrushless)
    private val motorFront = CANSparkMax(32, CANSparkLowLevel.MotorType.kBrushless)

    val beambreak = Beambreak(9)



    enum class State(val getVoltage: () -> DoubleArray) {
        INTAKE({ doubleArrayOf(4.0,9.36) }),
        LOAD({ doubleArrayOf(6.0,0.0)}),
        REVERSE({doubleArrayOf(-4.0,0.0)}),
        IDLE({ doubleArrayOf(0.0,0.0) });
    }

    init {
        motorBack.setIdleMode(CANSparkBase.IdleMode.kBrake)
        motorBack.setSmartCurrentLimit(50)
        motorBack.inverted = false
        motorBack.enableVoltageCompensation(11.0)

        motorFront.setIdleMode(CANSparkBase.IdleMode.kBrake)
        motorFront.setSmartCurrentLimit(30)
        motorFront.inverted = false
        motorFront.enableVoltageCompensation(11.0)

        RobotPeriodicManager.startPeriodic { trackState(); log() }
    }

    private fun trackState() {
        motorBack.setVoltage(intakeState.getVoltage()[0])
        motorFront.setVoltage(intakeState.getVoltage()[1])
    }

    private fun log() {
        Logger.log("Intake/frontMotor/RPM", motorFront.encoder.velocity)
        Logger.log("Intake/frontMotor/Amps", motorFront.outputCurrent)
        Logger.log("Intake/frontMotor/SetSpeed", motorFront.appliedOutput)

        Logger.log("Intake/backMotor/RPM", motorBack.encoder.velocity)
        Logger.log("Intake/backMotor/Amps", motorBack.outputCurrent)
        Logger.log("Intake/backMotor/SetSpeed", motorBack.appliedOutput)

        Logger.log("Intake/Beambreak", beambreak.isTripped())

        Logger.log("Intake/State", intakeState)
    }

    fun setState(state: State) {
        this.intakeState = state
    }
}