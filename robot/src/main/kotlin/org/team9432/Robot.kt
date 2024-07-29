package org.team9432

import edu.wpi.first.networktables.NetworkTable
import edu.wpi.first.networktables.NetworkTableInstance
import edu.wpi.first.wpilibj.PowerDistribution
import org.littletonrobotics.junction.LoggedCoroutineRobot
import org.littletonrobotics.junction.Logger
import org.littletonrobotics.junction.networktables.NT4Publisher
import org.team9432.commands.auto.auto
import org.team9432.lib.commandbased.KCommandScheduler
import org.team9432.subsystems.Drivetrain
import org.team9432.subsystems.Shooter


object Robot : LoggedCoroutineRobot() {
    val table: NetworkTable by lazy { NetworkTableInstance.getDefault().getTable("Elastic") }

    override fun robotPeriodic() {
        KCommandScheduler.run()


    }

    override fun robotInit() {
        super.robotInit()



        Shooter
        Drivetrain


        Logger.recordMetadata("ProjectName", "MyProject")
        //Logger.addDataReceiver(WPILOGWriter()) // Log to a USB stick ("/U/logs")
        Logger.addDataReceiver(NT4Publisher()) // Publish data to NetworkTables
        PowerDistribution(1, PowerDistribution.ModuleType.kRev) // Enables power distribution logging
        Logger.start()
    }

    override fun autonomousInit() {
        KCommandScheduler.schedule(auto())
    }

    override fun teleopInit() {
        Controls
    }

}