package org.team9432.oi


import org.team9432.Actions
import org.team9432.RobotController
import org.team9432.lib.input.XboxController

object Controls {
    val controller = XboxController(0)

    init {
        controller.x
            .onTrue { Actions.drop() }

        controller.b
            .onTrue { RobotController.setAction { Actions.intake() } }

        controller.a
            .onTrue { RobotController.setAction { Actions.startShooting() } }
            .onFalse { RobotController.setAction { Actions.stopShooting() } }

        controller.y
            .onTrue { RobotController.resetRequests(); RobotController.setAction { Actions.idle() } }

        controller.leftBumper
            .onTrue { RobotController.setAction { Actions.groundIntake() } }
    }
}