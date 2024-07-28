package org.team9432

import edu.wpi.first.wpilibj.GenericHID
import org.team9432.commands.driveTrain.slowDrive
import org.team9432.commands.driveTrain.teleDrive
import org.team9432.commands.shooter.intake
import org.team9432.commands.shooter.shoot
import org.team9432.commands.shooter.shootAmp
import org.team9432.commands.shooter.shootEnd
import org.team9432.lib.commandbased.KCommandScheduler
import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.lib.commandbased.commands.SimpleCommand
import org.team9432.lib.commandbased.input.KXboxController
import org.team9432.subsystems.Drivetrain
import org.team9432.subsystems.Shooter
import org.team9432.subsystems.driveTrain

object Controls {
    val controller = KXboxController(0, squareJoysticks = true)

    init {
        Drivetrain.defaultCommand = teleDrive({controller.getRawAxis(1) * .75}, {controller.getRawAxis(0) * .75})

        controller.leftBumper.whileTrue(slowDrive({controller.getRawAxis(1)}, {controller.getRawAxis(0)}))

        controller.a.onTrue(shoot())
        controller.a.onFalse(shootEnd())
        controller.b.onTrue(intake())
        controller.b.onFalse(SequentialCommand(Shooter.Commands.setTopSpeed(0.0),Shooter.Commands.setBottomSpeed(0.0)))
        controller.x.onTrue(shootAmp())
    }
}