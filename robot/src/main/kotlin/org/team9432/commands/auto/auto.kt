package org.team9432.commands.auto

import org.team9432.commands.shooter.shoot
import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.lib.commandbased.commands.WaitCommand
import org.team9432.subsystems.Drivetrain

fun auto() = SequentialCommand(
    shoot(),
    WaitCommand(3.25),
    Drivetrain.Commands.tankDrive(0.5,0.5),
    WaitCommand(1.5),
    Drivetrain.Commands.tankDrive(0.0,0.0)
)