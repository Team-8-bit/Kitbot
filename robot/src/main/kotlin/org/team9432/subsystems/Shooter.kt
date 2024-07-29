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
    val motorBottom = CANSparkMax(21, CANSparkLowLevel.MotorType.kBrushless)
    val motorTop = CANSparkMax(22, CANSparkLowLevel.MotorType.kBrushless)
    private val topPid = PIDController(0.0039231, 0.0, 0.0)
    private val bottomPid = PIDController(0.0039231, 0.0, 0.0)
    private var feedforward = SimpleMotorFeedforward(0.0, 0.0086634, 0.0038234)
    private val shooterTable = table.getSubTable("Shooter")
    private val noteInRobotPublisher: BooleanPublisher = shooterTable.getBooleanTopic("NoteInRobot").publish()

    private val bottomMotorTable = shooterTable.getSubTable("BottomMotor")
    private val bottomMotorRPMPublisher = bottomMotorTable.getDoubleTopic("RPM").publish()
    private val bottomMotorSetPointSpeedPublisher = bottomMotorTable.getDoubleTopic("SetPoint Speed").publish()
    private val bottomMotorAmpsPublisher = bottomMotorTable.getDoubleTopic("Amps").publish()
    private val bottomMotorVoltsPublisher = bottomMotorTable.getDoubleTopic("Volts").publish()

    private val topMotorTable = shooterTable.getSubTable("TopMotor")
    private val topMotorRPMPublisher =   topMotorTable.getDoubleTopic("RPM").publish()
    private val topMotorSetPointSpeedPublisher = topMotorTable.getDoubleTopic("SetPoint Speed").publish()
    private val topMotorAmpsPublisher =  topMotorTable.getDoubleTopic("Amps").publish()
    private val topMotorVoltsPublisher = topMotorTable.getDoubleTopic("Volts").publish()




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

        bottomMotorRPMPublisher.set(motorBottom.encoder.velocity)
        bottomMotorSetPointSpeedPublisher.set(bottomPid.setpoint)
        bottomMotorAmpsPublisher.set(motorBottom.outputCurrent)
        bottomMotorVoltsPublisher.set(motorBottom.appliedOutput)

        topMotorRPMPublisher.set(motorTop.encoder.velocity)
        topMotorSetPointSpeedPublisher.set(topPid.setpoint)
        topMotorAmpsPublisher.set(motorTop.outputCurrent)
        topMotorVoltsPublisher.set(motorTop.appliedOutput)

    }

    fun setBottomSpeed(speed: Double) {
        motorBottom.setVoltage(bottomPid.calculate(speed) + feedforward.calculate(bottomPid.setpoint))
    }

    fun setTopSpeed(speed: Double) {
        motorTop.setVoltage(topPid.calculate(speed) + feedforward.calculate(topPid.setpoint))
    }

    object Commands {
        fun setNoteInRobot(boolean: Boolean) = InstantCommand(Shooter) { note = boolean }
        fun setTopSpeed(speed: Double) = InstantCommand(Shooter) { Shooter.setTopSpeed(speed) }
        fun setBottomSpeed(speed: Double) = InstantCommand(Shooter) { Shooter.setBottomSpeed(speed) }
    }

}