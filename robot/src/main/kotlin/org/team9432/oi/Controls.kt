package org.team9432.oi


import org.team9432.Actions
import org.team9432.Robot
import org.team9432.RobotController
import org.team9432.lib.RobotPeriodicManager
import org.team9432.lib.coroutines.CoroutineRobot
import org.team9432.lib.input.XboxController
import org.team9432.resources.Drivetrain.arcadeDrive


object Controls {
    val controller = XboxController(0)

    suspend fun bind(){
        controller.x
            .onTrue { Actions.drop() }

        controller.b
            .onTrue{ RobotController.setAction { Actions.intake() } }
            .onFalse{ RobotController.setAction { Actions.stopIntaking() } }

        controller.a
            .onTrue{ RobotController.setAction { Actions.startShooting() } }
            .onFalse{ RobotController.setAction { Actions.stopShooting() } }

        controller.y
            .onTrue { RobotController.resetRequests(); RobotController.setAction { Actions.idle() }}
        RobotPeriodicManager.startPeriodic { drive() }

        controller.leftBumper
            .onTrue { RobotController.setAction { Actions.groundIntake() } }
    }

    private fun drive() {
        if(Robot.mode == CoroutineRobot.Mode.TELEOP){
            arcadeDrive(controller.leftY, controller.leftX - controller.leftTriggerAxis + controller.rightTriggerAxis)
        }
    }

}