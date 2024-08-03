package org.team9432.commands.shooter

import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.lib.commandbased.commands.WaitCommand
import org.team9432.subsystems.Shooter

fun shootEnd() = SequentialCommand(
    Shooter.Commands.setBottomSpeed(3500.0),
    WaitCommand(0.5),
    Shooter.Commands.setBottomSpeed(0.0),
    Shooter.Commands.setTopSpeed(0.0),
    Shooter.Commands.setSideSpeed(0.0),
    Shooter.Commands.setNoteInRobot(false)
)