@file:JvmName("Main") // set the compiled Java class name to "Main" rather than "MainKt"
package org.team9432

import com.revrobotics.CANSparkBase
import edu.wpi.first.wpilibj.PowerDistribution
import edu.wpi.first.wpilibj.RobotBase
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import org.littletonrobotics.junction.LogFileUtil
import org.littletonrobotics.junction.Logger
import org.littletonrobotics.junction.networktables.NT4Publisher
import org.littletonrobotics.junction.wpilog.WPILOGReader
import org.littletonrobotics.junction.wpilog.WPILOGWriter
import org.team9432.lib.Library
import org.team9432.lib.coroutines.LoggedCoroutineRobot
import org.team9432.lib.coroutines.Team8BitRobot.Runtime.*
import org.team9432.lib.resource.Action
import org.team9432.oi.Controls
import org.team9432.resources.Drivetrain
import org.team9432.resources.intake.Intake
import org.team9432.resources.loader.Loader
import org.team9432.resources.shooter.Shooter

object Robot : LoggedCoroutineRobot() {
    val runtime = if (RobotBase.isReal()) REAL else SIM

    private val autoChooser = SendableChooser<Action>()
    
    override suspend fun init() {

        Logger.recordMetadata("ProjectName", "2024-KitBot") // Set a metadata value
        Logger.recordMetadata("GIT_SHA", GIT_SHA)
        Logger.recordMetadata("GIT_DATE", GIT_DATE)
        Logger.recordMetadata("GIT_BRANCH", GIT_BRANCH)
        Logger.recordMetadata("BUILD_DATE", BUILD_DATE)
        Logger.recordMetadata("DIRTY", if (DIRTY == 1) "true" else "false")

        when (runtime) {
            REAL -> {
                Logger.addDataReceiver(WPILOGWriter()) // Log to a USB stick ("/U/logs")
                Logger.addDataReceiver(NT4Publisher()) // Publish data to NetworkTables
                PowerDistribution(1, PowerDistribution.ModuleType.kRev) // Enables power distribution logging
            }

            SIM -> {
                Logger.addDataReceiver(NT4Publisher())
                PowerDistribution(1, PowerDistribution.ModuleType.kRev) // Enables power distribution logging
            }

            REPLAY -> {
                setUseTiming(false) // Run as fast as possible
                val logPath = LogFileUtil.findReplayLog() // Pull the replay log from AdvantageScope (or prompt the user)
                Logger.setReplaySource(WPILOGReader(logPath)) // Read replay log
                Logger.addDataReceiver(WPILOGWriter(LogFileUtil.addPathSuffix(logPath, "_replay"))) // Save outputs to a new log
            }
        }

        Logger.start() // Start logging! No more data receivers, replay sources, or metadata values may be added.

        Library.initialize(this, runtime)

        Shooter
        Loader
        Drivetrain
        Intake

        Controls

        LEDs


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