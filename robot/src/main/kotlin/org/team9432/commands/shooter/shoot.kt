package org.team9432.commands.shooter

import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.lib.commandbased.commands.WaitCommand
import org.team9432.subsystems.Shooter

fun shoot() = SequentialCommand(
    Shooter.Commands.setTopSpeed(9000.0),
    WaitCommand(2.0),
)