package org.team9432

import com.revrobotics.CANSparkBase
import edu.wpi.first.networktables.NetworkTable
import edu.wpi.first.networktables.NetworkTableInstance
import edu.wpi.first.wpilibj.DataLogManager
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.PowerDistribution
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilderImpl
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser
import org.littletonrobotics.junction.LoggedCoroutineRobot
import org.littletonrobotics.junction.Logger
import org.littletonrobotics.junction.networktables.NT4Publisher
import org.team9432.commands.auto.auto
import org.team9432.commands.auto.auto2
import org.team9432.lib.commandbased.KCommandScheduler
import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.subsystems.Drivetrain
import org.team9432.subsystems.Drivetrain.setIdleMode
import org.team9432.subsystems.Shooter


object Robot : LoggedCoroutineRobot() {
    val table: NetworkTable by lazy { NetworkTableInstance.getDefault().getTable("Elastic") }
    private val autoChooserBuilder = SendableBuilderImpl()
    private val autoChooser = SendableChooser<SequentialCommand>()

    override fun robotPeriodic() {
        KCommandScheduler.run()

        autoChooserBuilder.update()

    }



    override fun robotInit() {
        super.robotInit()

        DataLogManager.start()
        DriverStation.startDataLog(DataLogManager.getLog())
        DataLogManager.logNetworkTables(true)

        Shooter
        Drivetrain


        Logger.recordMetadata("ProjectName", "MyProject")
        //Logger.addDataReceiver(WPILOGWriter()) // Log to a USB stick ("/U/logs")
        Logger.addDataReceiver(NT4Publisher()) // Publish data to NetworkTables
        PowerDistribution(1, PowerDistribution.ModuleType.kRev) // Enables power distribution logging
        Logger.start()

        autoChooserBuilder.table = table.getSubTable("autoChooser")
        autoChooser.addOption("Auto 1", auto())
        autoChooser.addOption("Auto 2", auto2())
        autoChooser.setDefaultOption("Auto 1", auto())
        autoChooser.initSendable(autoChooserBuilder)
        autoChooserBuilder.startListeners()




    }

    override fun disabledInit() {
        super.disabledInit()
        setIdleMode(CANSparkBase.IdleMode.kCoast)
    }

    override fun disabledExit() {
        super.disabledExit()
        setIdleMode(CANSparkBase.IdleMode.kBrake)
    }

    override fun autonomousInit() {
        KCommandScheduler.schedule(autoChooser.selected)
    }

    override fun teleopInit() {
        Controls
    }

}