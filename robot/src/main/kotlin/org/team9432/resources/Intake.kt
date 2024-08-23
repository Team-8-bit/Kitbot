package org.team9432.resources

import com.revrobotics.CANSparkBase
import com.revrobotics.CANSparkLowLevel
import com.revrobotics.CANSparkMax
import edu.wpi.first.units.Voltage
import org.team9432.lib.RobotPeriodicManager
import org.team9432.lib.resource.Resource
import org.team9432.lib.doglog.Logger

object Intake: Resource("Intake") {
    var intakeState = State.IDLE

    private val motorFront = CANSparkMax(31, CANSparkLowLevel.MotorType.kBrushless)
    private val motorBack = CANSparkMax(32, CANSparkLowLevel.MotorType.kBrushless)



    enum class State(val getVoltage: () -> DoubleArray) {
        INTAKE({ doubleArrayOf(4.0,9.36) }),
        IDLE({ doubleArrayOf(0.0,0.0) });
    }

    init {
        motorFront.setIdleMode(CANSparkBase.IdleMode.kBrake)
        motorFront.setSmartCurrentLimit(60)
        motorFront.inverted = false
        motorFront.enableVoltageCompensation(11.0)

        motorBack.setIdleMode(CANSparkBase.IdleMode.kBrake)
        motorBack.setSmartCurrentLimit(60)
        motorBack.inverted = true
        motorBack.enableVoltageCompensation(11.0)

        RobotPeriodicManager.startPeriodic { trackState(); log() }
    }

    private fun trackState() {
        motorFront.setVoltage(intakeState.getVoltage()[0])
        motorBack.setVoltage(intakeState.getVoltage()[1])
    }

    private fun log() {
        Logger.log("Intake/backMotor/RPM", motorBack.encoder.velocity)
        Logger.log("Intake/backMotor/Amps", motorBack.outputCurrent)
        Logger.log("Intake/backMotor/SetSpeed", motorBack.appliedOutput)

        Logger.log("Intake/frontMotor/RPM", motorFront.encoder.velocity)
        Logger.log("Intake/frontMotor/Amps", motorFront.outputCurrent)
        Logger.log("Intake/frontMotor/SetSpeed", motorFront.appliedOutput)

        Logger.log("Intake/State", intakeState)
    }

    fun setState(state: State) {
        this.intakeState = state
    }
}