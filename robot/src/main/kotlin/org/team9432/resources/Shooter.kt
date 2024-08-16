package org.team9432.resources

import com.revrobotics.CANSparkBase
import com.revrobotics.CANSparkLowLevel
import com.revrobotics.CANSparkMax
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.controller.SimpleMotorFeedforward
import org.team9432.lib.RobotPeriodicManager
import org.team9432.lib.doglog.Logger
import org.team9432.lib.resource.Resource

object Shooter : Resource("Shooter") {
    var shooterState = State.IDLE

    var note = false

    val motorTop = CANSparkMax(22, CANSparkLowLevel.MotorType.kBrushless)
    val motorSide = CANSparkMax(23, CANSparkLowLevel.MotorType.kBrushless)

    private val topPid = PIDController(0.0039231, 0.0, 0.0)
    private val sidePid = PIDController(0.0039231, 0.0, 0.0)
    private var feedforward = SimpleMotorFeedforward(0.0, 0.0086634, 0.0038234)//TODO Tune feedforward

    enum class State(val getSpeed: () -> DoubleArray) {
        SHOOT({ doubleArrayOf(6500.0,6500.0) }),
        INTAKE({ doubleArrayOf(-3750.0,-3750.0) }),
        DROP({ doubleArrayOf(300.0,300.0) }),
        IDLE({ doubleArrayOf(0.0,0.0) });
    }

    init {

        motorTop.setIdleMode(CANSparkBase.IdleMode.kCoast)
        motorTop.setSmartCurrentLimit(60)
        motorTop.inverted = true

        motorSide.setIdleMode(CANSparkBase.IdleMode.kCoast)
        motorSide.setSmartCurrentLimit(60)
        motorSide.inverted = false

        RobotPeriodicManager.startPeriodic { trackState(); log() }
    }

    private fun trackState() {
        motorTop.setVoltage(topPid.calculate(shooterState.getSpeed()[0]) + feedforward.calculate(topPid.setpoint)/2)
        motorSide.setVoltage(sidePid.calculate(shooterState.getSpeed()[1]) + feedforward.calculate(sidePid.setpoint)/2)
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

        Logger.log("Shooter/State", shooterState)
    }

    fun setState(state: State) {
        this.shooterState = state
    }

}