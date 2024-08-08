package org.team9432.commands.shooter

import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.lib.commandbased.commands.SimpleCommand
import org.team9432.lib.commandbased.commands.WaitCommand
import org.team9432.subsystems.Shooter

fun shoot() = SequentialCommand(
    Shooter.Commands.setTopSpeed(6500.0),
    Shooter.Commands.setSideSpeed(6500.0)//TODO Tune speeds
)