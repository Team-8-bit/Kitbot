package org.team9432

import org.team9432.commands.shooter.intake
import org.team9432.commands.shooter.shoot
import org.team9432.commands.shooter.shootAmp
import org.team9432.commands.shooter.shootEnd
import org.team9432.lib.commandbased.KCommandScheduler
import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.lib.commandbased.input.KXboxController
import org.team9432.subsystems.Shooter

object Controls {
    private val controller = KXboxController(0, squareJoysticks = false)

    init {
        controller.a.onTrue(shoot())
        controller.a.onFalse(shootEnd())
        controller.b.onTrue(intake())
        controller.b.onFalse(SequentialCommand(Shooter.Commands.setTopSpeed(0.0),Shooter.Commands.setBottomSpeed(0.0)))
        controller.x.onTrue(shootAmp())
    }
}