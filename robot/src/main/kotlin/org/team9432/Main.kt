@file:JvmName("Main") // set the compiled Java class name to "Main" rather than "MainKt"
package org.team9432

import edu.wpi.first.networktables.NetworkTable
import edu.wpi.first.networktables.NetworkTableInstance
import edu.wpi.first.wpilibj.RobotBase
import edu.wpi.first.wpilibj.XboxController
import org.team9432.lib.coroutines.CoroutineRobot
import org.team9432.lib.doglog.Logger

import org.team9432.oi.Buttons
import org.team9432.oi.Buttons.controller
import org.team9432.resources.Drivetrain
import org.team9432.resources.Loader
import org.team9432.resources.Shooter

object Robot : CoroutineRobot() {
//    val table: NetworkTable by lazy { NetworkTableInstance.getDefault().getTable("Elastic") }
//    private val autoChooserBuilder = SendableBuilderImpl()
//    private val autoChooser = SendableChooser<SequentialCommand>()
//
//    override fun robotPeriodic() {
//        autoChooserBuilder.update()
//    }
    override suspend fun periodic() {
        super.periodic()
        //println( controller.getRawButton(2))
        //println("is connected "+controller.isConnected())
    }
    override suspend fun init() {
        Logger.configureDevelopmentDefaults()


        Shooter
        Loader
        Drivetrain

        Buttons.bind()


//        DataLogManager.start()
//        DriverStation.startDataLog(DataLogManager.getLog())
//        DataLogManager.logNetworkTables(true)
//        Logger.recordMetadata("ProjectName", "MyProject")
//        //Logger.addDataReceiver(WPILOGWriter()) // Log to a USB stick ("/U/logs")
//        Logger.addDataReceiver(NT4Publisher()) // Publish data to NetworkTables
//        PowerDistribution(1, PowerDistribution.ModuleType.kRev) // Enables power distribution logging
//        Logger.start()

//        autoChooserBuilder.table = table.getSubTable("autoChooser")
//        autoChooser.addOption("Auto 1", auto())
//        autoChooser.addOption("Auto 2", auto2())
//        autoChooser.setDefaultOption("Auto 1", auto())
//        autoChooser.initSendable(autoChooserBuilder)
//        autoChooserBuilder.startListeners()

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