package org.team9432.commands.driveTrain

import org.team9432.commands.shooter.shoot
import org.team9432.commands.shooter.shootEnd
import org.team9432.lib.commandbased.KCommand
import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.lib.commandbased.commands.SimpleCommand
import org.team9432.lib.commandbased.commands.WaitCommand
import org.team9432.subsystems.Drivetrain
import org.team9432.subsystems.Shooter

fun slowDrive(speed:() -> Double, rotation:() -> Double)  = SimpleCommand(
    requirements = setOf(Drivetrain),
    execute = {
        Drivetrain.arcadeDrive(speed.invoke() * 0.75,rotation.invoke() * 0.75)
    }
)