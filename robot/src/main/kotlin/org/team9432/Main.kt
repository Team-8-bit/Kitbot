@file:JvmName("Main") // set the compiled Java class name to "Main" rather than "MainKt"
package org.team9432

import com.revrobotics.CANSparkBase
import edu.wpi.first.wpilibj.RobotBase
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import org.team9432.lib.coroutines.CoroutineRobot
import org.team9432.lib.doglog.Logger
import org.team9432.lib.resource.Action
import org.team9432.oi.Controls
import org.team9432.resources.Drivetrain
import org.team9432.resources.Intake
import org.team9432.resources.Loader
import org.team9432.resources.Shooter

object Robot : CoroutineRobot(useActionManager = false) {
    private val autoChooser = SendableChooser<Action>()
    override suspend fun periodic() {
        super.periodic()
    }
    override suspend fun init() {
        Logger.configure(ntPublish = true, captureNt = true, captureDs = true, logExtras = true, logEntryQueueCapacity = 1000)

        Shooter
        Loader
        Drivetrain
        Intake

        LEDs

        Controls.bind()
        autoChooser.addOption("Shoot Only") { Auto.onlyShoot() }
        autoChooser.addOption("Shoot And Drive") { Auto.shootAndDrive() }
        autoChooser.addOption("Basic Two Note") { Auto.basicTwoNote() }
        autoChooser.setDefaultOption("Shoot And Drive") { Auto.shootAndDrive() }

        SmartDashboard.putData(autoChooser)
    }

    override suspend fun disabled() {
        super.disabled()
        Drivetrain.setIdleMode(CANSparkBase.IdleMode.kCoast)
        RobotController.resetRequests()
    }

    override suspend fun autonomous() {
        super.autonomous()
        Drivetrain.setIdleMode(CANSparkBase.IdleMode.kBrake)
        RobotController.setAction(autoChooser.selected)
    }

    override suspend fun teleop() {
        super.teleop()
        Drivetrain.setIdleMode(CANSparkBase.IdleMode.kBrake)
    }


}
/**
 * Main initialization function. Do not perform any initialization here
 * other than calling `RobotBase.startRobot`. Do not modify this file
 * except to change the object passed to the `startRobot` call.
 *
 * If you change the package of this file, you must also update the
 * `ROBOT_MAIN_CLASS` variable in the gradle build file. Note that
 * this file has a `@file:JvmName` annotation so that its compiled
 * Java class name is "Main" rather than "MainKt". This is to prevent
 * any issues/confusion if this file is ever replaced with a Java class.
 *
 * If you change your main Robot object (name), change the parameter of the
 * `RobotBase.startRobot` call to the new name. (If you use the IDE's Rename
 * Refactoring when renaming the object, it will get changed everywhere
 * including here.)
 */
fun main() = RobotBase.startRobot { Robot }