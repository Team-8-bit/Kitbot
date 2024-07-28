package org.team9432.subsystems

import com.revrobotics.CANSparkBase
import com.revrobotics.CANSparkLowLevel
import com.revrobotics.CANSparkMax
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.controller.SimpleMotorFeedforward
import edu.wpi.first.networktables.BooleanPublisher
import org.team9432.Robot.table
import org.team9432.lib.commandbased.KSubsystem
import org.team9432.lib.commandbased.commands.InstantCommand

object Shooter : KSubsystem() {
    var note = false
    private val motorBottom = CANSparkMax(21, CANSparkLowLevel.MotorType.kBrushless)
    private val motorTop = CANSparkMax(22, CANSparkLowLevel.MotorType.kBrushless)
    private val topPid = PIDController(0.0039231, 0.0, 0.0)
    private val bottomPid = PIDController(0.0039231, 0.0, 0.0)
    private var feedforward = SimpleMotorFeedforward(0.0, 0.0086634, 0.0038234)
    val noteInRobotPublisher: BooleanPublisher = table.getBooleanTopic("Shooter/NoteInRobot").publish()


    init {
        motorBottom.setIdleMode(CANSparkBase.IdleMode.kCoast)
        motorTop.setIdleMode(CANSparkBase.IdleMode.kCoast)
        motorBottom.setSmartCurrentLimit(60)
        motorTop.setSmartCurrentLimit(60)
        motorTop.inverted = true
        motorBottom.inverted = true
    }


    override fun periodic() {
        noteInRobotPublisher.set(note)
    }

    fun setBottomSpeed(speed: Double) {
        motorBottom.setVoltage(bottomPid.calculate(speed) + feedforward.calculate(bottomPid.setpoint))
    }

    fun setTopSpeed(speed: Double) {
        motorTop.setVoltage(topPid.calculate(speed) + feedforward.calculate(topPid.setpoint))
    }

    fun getTopAmps(): Double {
        return motorTop.outputCurrent
    }

    fun getBottomAmps(): Double {
        return motorBottom.outputCurrent
    }

    fun getTopRPM(): Double {
        return motorTop.encoder.velocity
    }

    fun getBottomRPM(): Double {
        return motorBottom.encoder.velocity
    }

    fun getNoteInRobot(): Boolean {
        return note
    }

    fun setNoteInRobot(boolean: Boolean) {
        note = boolean
    }

    object Commands {
        fun setNoteInRobot(boolean: Boolean) = InstantCommand(Shooter) { note = boolean }
        fun setTopSpeed(speed: Double) = InstantCommand(Shooter) { Shooter.setTopSpeed(speed) }
        fun setBottomSpeed(speed: Double) = InstantCommand(Shooter) { Shooter.setBottomSpeed(speed) }
    }

}