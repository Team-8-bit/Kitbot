package org.team9432

import edu.wpi.first.wpilibj.PowerDistribution
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import org.littletonrobotics.junction.LoggedCoroutineRobot
import org.littletonrobotics.junction.Logger
import org.littletonrobotics.junction.networktables.NT4Publisher
import org.littletonrobotics.junction.wpilog.WPILOGWriter
import org.team9432.commands.auto.auto
import org.team9432.lib.commandbased.KCommandScheduler
import org.team9432.lib.commandbased.input.KXboxController
import org.team9432.subsystems.Drivetrain
import org.team9432.subsystems.Shooter


object Robot: LoggedCoroutineRobot() {
    private val controller = KXboxController(0, squareJoysticks = true)
    override fun robotPeriodic()  {
        KCommandScheduler.run()
        Logger.recordOutput("Shooter/BottomRPM", Shooter.getBottomRPM())
        Logger.recordOutput("Shooter/BottomAmps", Shooter.getBottomAmps())

        Logger.recordOutput("Shooter/TopRPM", Shooter.getTopRPM())
        Logger.recordOutput("Shooter/TopAmps", Shooter.getTopAmps())
//        SmartDashboard.putNumber("Top Speed", Shooter.getTopRPM())
//        Shooter.setTopSpeed(SmartDashboard.getNumber("set Top Speed", 0.0))
//        SmartDashboard.putBoolean("Note in robot",Shooter.getNoteInRobot())
//        SmartDashboard.putNumber("Bottom RPM", Shooter.getBottomRPM())
    }

    override fun robotInit() {
        super.robotInit()
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

    override fun teleopPeriodic() {
        super.teleopPeriodic()
        //Drivetrain.arcadeDrive(controller.getRawAxis(1), controller.getRawAxis(0) *0.75)
    }

}