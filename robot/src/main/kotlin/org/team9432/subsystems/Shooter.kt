package org.team9432.subsystems

import com.revrobotics.CANSparkBase
import com.revrobotics.CANSparkLowLevel
import com.revrobotics.CANSparkMax
import org.team9432.lib.commandbased.KSubsystem
import org.team9432.lib.commandbased.commands.InstantCommand

object Shooter: KSubsystem() {

    private val motorBottom = CANSparkMax(21, CANSparkLowLevel.MotorType.kBrushless)
    private val motorTop = CANSparkMax(22, CANSparkLowLevel.MotorType.kBrushless)
    init {
        motorBottom.setIdleMode(CANSparkBase.IdleMode.kCoast)
        motorTop.setIdleMode(CANSparkBase.IdleMode.kCoast)
        motorBottom.setSmartCurrentLimit(60)
        motorTop.setSmartCurrentLimit(60)
    }
    fun setBottomSpeed(speed: Double){
        motorBottom.set(speed)
    }
    fun setTopSpeed(speed: Double){
        motorTop.set(speed)
    }
    fun getTopVoltage(): Double {
        return motorTop.outputCurrent
    }
    fun getBottomVoltage(): Double {
        return motorBottom.outputCurrent
    }

    object Commands {
        fun setTopSpeed(speed: Double) = InstantCommand(Shooter) { Shooter.setTopSpeed(speed) }
        fun setBottomSpeed(speed: Double) = InstantCommand(Shooter) { Shooter.setBottomSpeed(speed) }
    }

}