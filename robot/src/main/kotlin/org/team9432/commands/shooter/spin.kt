package org.team9432.commands.shooter

import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.lib.commandbased.commands.WaitCommand
import org.team9432.subsystems.Shooter

fun spin() = SequentialCommand(
    Shooter.Commands.setTopSpeed(400.0),
    Shooter.Commands.setSideSpeed(-800.0),//TODO Tune speeds
)