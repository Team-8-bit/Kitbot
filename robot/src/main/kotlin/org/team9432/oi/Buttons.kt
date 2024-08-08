package org.team9432.oi


import org.team9432.Actions
import org.team9432.Robot
import org.team9432.lib.coroutines.CoroutineRobot
import org.team9432.lib.input.XboxController
import org.team9432.resources.Drivetrain.arcadeDrive


object Buttons {
    private val controller = XboxController(0)

    suspend fun bind(){
        controller.b
            .onTrue{ Actions.intake() }
            .onFalse{ Actions.stopIntaking()}

        controller.a
            .onTrue{ Actions.startShooting() }
            .onFalse{ Actions.stopShooting() }
        CoroutineRobot.startPeriodic { drive() }
    }

    private fun drive() {
        if(Robot.isTeleop){
            arcadeDrive(Buttons.getJoystickDrive(), Buttons.getJoystickRotation())
        }
    }

    private fun getJoystickRotation(): Double {
        return controller.leftX
    }

    private fun getJoystickDrive(): Double {
        return controller.leftY
    }
}