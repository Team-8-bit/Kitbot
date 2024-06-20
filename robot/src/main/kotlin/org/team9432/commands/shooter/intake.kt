package org.team9432.commands.shooter

import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.lib.commandbased.commands.WaitCommand
import org.team9432.lib.commandbased.commands.WaitUntilCommand
import org.team9432.subsystems.Shooter


fun intake() = SequentialCommand(
    Shooter.Commands.setTopSpeed(-1.0),
    Shooter.Commands.setBottomSpeed(-1.0),
    WaitCommand(0.1),
    WaitUntilCommand(({ Shooter.getBottomVoltage() > 50 })),
    Shooter.Commands.setBottomSpeed(0.0),
    Shooter.Commands.setTopSpeed(0.0)




)