package org.team9432.resources

import com.revrobotics.CANSparkBase
import com.revrobotics.CANSparkLowLevel
import com.revrobotics.CANSparkMax
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.controller.SimpleMotorFeedforward
import org.littletonrobotics.junction.Logger
import org.team9432.lib.RobotPeriodicManager
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
        INTAKE({ doubleArrayOf(-1700.0,-1700.0) }),
        DROP({ doubleArrayOf(500.0,500.0) }),
        IDLE({ doubleArrayOf(0.0,0.0) });
    }

    init {

        motorTop.setIdleMode(CANSparkBase.IdleMode.kCoast)
        motorTop.setSmartCurrentLimit(60)
        motorTop.inverted = true

        motorSide.setIdleMode(CANSparkBase.IdleMode.kCoast)
        motorSide.setSmartCurrentLimit(60)
        motorSide.inverted = false

        motorTop.burnFlash()
        motorSide.burnFlash()

        RobotPeriodicManager.startPeriodic { trackState(); log() }
    }

    private fun trackState() {
        motorTop.setVoltage(topPid.calculate(shooterState.getSpeed()[0])/2 + feedforward.calculate(topPid.setpoint))
        motorSide.setVoltage(sidePid.calculate(shooterState.getSpeed()[1])/2 + feedforward.calculate(sidePid.setpoint))
    }

    fun log() {

        Logger.recordOutput("Shooter/sideMotor/RPM", motorSide.encoder.velocity)
        Logger.recordOutput("Shooter/sideMotor/Amps", motorSide.outputCurrent)
        Logger.recordOutput("Shooter/sideMotor/SetSpeed", motorSide.appliedOutput)

        Logger.recordOutput("Shooter/topMotor/RPM", motorTop.encoder.velocity)
        Logger.recordOutput("Shooter/topMotor/Amps", motorTop.outputCurrent)
        Logger.recordOutput("Shooter/topMotor/SetSpeed", motorTop.appliedOutput)

        Logger.recordOutput("Shooter/NoteInRobot", note)

        Logger.recordOutput("Shooter/State", shooterState)
    }

    fun setState(state: State) {
        this.shooterState = state
    }

}