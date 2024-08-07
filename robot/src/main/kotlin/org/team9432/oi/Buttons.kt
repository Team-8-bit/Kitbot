package org.team9432.oi


import org.team9432.Actions
import org.team9432.lib.input.XboxController


object Buttons {
    val controller = XboxController(0)

    suspend fun bind(){

        controller.b
            .onTrue{ Actions.intake() }
            .onFalse{ Actions.stopIntaking()}

        controller.a
            .onTrue{ Actions.startShooting() }
            .onFalse{ Actions.stopShooting() }
    }
}