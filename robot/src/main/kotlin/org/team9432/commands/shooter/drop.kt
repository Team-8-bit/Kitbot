package org.team9432.commands.shooter

import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.lib.commandbased.commands.WaitCommand
import org.team9432.subsystems.Shooter

fun shootAmp() = SequentialCommand(
    Shooter.Commands.setTopSpeed(400.0),
    Shooter.Commands.setSideSpeed(400.0),
    Shooter.Commands.setBottomSpeed(2500.0),
    WaitCommand(0.25),
    stop(),
    Shooter.Commands.setNoteInRobot(false)
)