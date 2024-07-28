package org.team9432.commands.driveTrain

import org.team9432.lib.commandbased.commands.SimpleCommand
import org.team9432.subsystems.Drivetrain

fun teleDrive(speed: () -> Double, rotation: () -> Double) = SimpleCommand(
    requirements = setOf(Drivetrain),
    execute = {
        Drivetrain.arcadeDrive(speed.invoke(), rotation.invoke())
    }
)