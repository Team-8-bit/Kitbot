@file:JvmName("Main") // set the compiled Java class name to "Main" rather than "MainKt"
package org.team9432

import com.pathplanner.lib.auto.AutoBuilder
import com.pathplanner.lib.auto.NamedCommands
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.PowerDistribution
import edu.wpi.first.wpilibj.RobotBase
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.CommandScheduler
import edu.wpi.first.wpilibj2.command.InstantCommand
import kotlinx.coroutines.launch
import org.littletonrobotics.junction.LogFileUtil
import org.littletonrobotics.junction.Logger
import org.littletonrobotics.junction.networktables.NT4Publisher
import org.littletonrobotics.junction.wpilog.WPILOGReader
import org.littletonrobotics.junction.wpilog.WPILOGWriter
import org.team9432.lib.Library
import org.team9432.lib.coroutines.LoggedCoroutineRobot
import org.team9432.lib.coroutines.Team8BitRobot.Runtime.*
import org.team9432.oi.Controls
import org.team9432.resources.drivetrain.Drivetrain
import org.team9432.resources.intake.Intake
import org.team9432.resources.loader.Loader
import org.team9432.resources.shooter.Shooter
import org.team9432.resources.vision.Vision


object Robot : LoggedCoroutineRobot() {
    val runtime = if (RobotBase.isReal()) REAL else SIM
    val Scheduler = CommandScheduler.getInstance()
    lateinit var autoChooser: SendableChooser<Command>

    override suspend fun init() {

        Logger.recordMetadata("ProjectName", "2024-KitBot") // Set a metadata value
        Logger.recordMetadata("GIT_SHA", GIT_SHA)
        Logger.recordMetadata("GIT_DATE", GIT_DATE)
        Logger.recordMetadata("GIT_BRANCH", GIT_BRANCH)
        Logger.recordMetadata("BUILD_DATE", BUILD_DATE)
        Logger.recordMetadata("DIRTY", if (DIRTY == 1) "true" else "false")

        when (runtime) {
            REAL -> {
                //Logger.addDataReceiver(WPILOGWriter()) // Log to a USB stick ("/U/logs")
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

        Vision


        Controls

        LEDs

        NamedCommands.registerCommand("Intake", InstantCommand( {
            coroutineScope.launch {
                Actions.groundIntake()
            }
        }))
        NamedCommands.registerCommand("StartShooting", InstantCommand( {
            coroutineScope.launch {
                Actions.startShooting()
            }
        }))
        NamedCommands.registerCommand("StopShooting", InstantCommand( {
            coroutineScope.launch {
                Actions.stopShooting()
            }
        }))


        autoChooser = AutoBuilder.buildAutoChooser()

        SmartDashboard.putData("Auto Chooser", autoChooser);

        DriverStation.silenceJoystickConnectionWarning(true)

    }

    override suspend fun disabled() {
        super.disabled()
        RobotController.resetRequests()
    }

    override suspend fun autonomous() {
//        RobotController.setAction {
//            val selectedAuto = AutoChooser.getAuto()
//
//            if (selectedAuto == null) {
//                println("[Error] Auto was null")
//                return@setAction
//            }
//
//            selectedAuto.run()
//        }

        //PathPlannerAuto("2 note (NOTE3)").schedule()
        autoChooser.selected.schedule()


    }

    override fun autonomousPeriodic() {
        Scheduler.run()
    }

    override suspend fun teleop() {}


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