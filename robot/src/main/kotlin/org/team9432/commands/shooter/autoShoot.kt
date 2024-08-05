package org.team9432.commands.shooter

import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.lib.commandbased.commands.WaitCommand
import org.team9432.subsystems.Shooter

fun autoShoot() = SequentialCommand(
    Shooter.Commands.setTopSpeed(9000.0),
    Shooter.Commands.setSideSpeed(3750.0),//TODO Tune speeds
    WaitCommand(2.5),
    Shooter.Commands.setBottomSpeed(2500.0),
    WaitCommand(0.5),
    Shooter.Commands.setBottomSpeed(0.0),
    Shooter.Commands.setTopSpeed(0.0),
    Shooter.Commands.setSideSpeed(0.0),
    Shooter.Commands.setNoteInRobot(false)
)