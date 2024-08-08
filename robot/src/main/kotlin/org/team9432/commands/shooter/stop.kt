package org.team9432.commands.shooter

import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.subsystems.Shooter

fun stop() = SequentialCommand(
    Shooter.Commands.setTopSpeed(0.0),
    Shooter.Commands.setSideSpeed(0.0),
    Shooter.Commands.setBottomSpeed(0.0)
)