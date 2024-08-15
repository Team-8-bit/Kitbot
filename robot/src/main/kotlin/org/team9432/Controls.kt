package org.team9432


import org.team9432.commands.driveTrain.slowDrive
import org.team9432.commands.driveTrain.teleDrive
import org.team9432.commands.shooter.*
import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.lib.commandbased.input.KXboxController
import org.team9432.subsystems.Drivetrain
import org.team9432.subsystems.Shooter

object Controls {
    private val controller = KXboxController(0, squareJoysticks = true)

    init {
        Drivetrain.defaultCommand = teleDrive({ controller.getRawAxis(1) * .75 }, { (controller.getRawAxis(0) * .75) - controller.leftTriggerAxis + controller.rightTriggerAxis })
        controller.leftBumper.whileTrue(slowDrive({ controller.getRawAxis(1) }, { controller.getRawAxis(0) }))

        controller.b
            .onTrue(intake())
            .onFalse(stop())
        controller.x.onTrue(shootAmp())
        controller.a
            .onTrue(shoot())
            .onFalse(shootEnd())
        controller.y
            .onTrue(spin())
            .onFalse(stop())
    }
}