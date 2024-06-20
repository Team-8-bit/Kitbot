package org.team9432

import edu.wpi.first.wpilibj.XboxController
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import org.littletonrobotics.junction.LoggedCoroutineRobot
import org.team9432.commands.auto.auto
import org.team9432.lib.commandbased.KCommandScheduler
import org.team9432.subsystems.Drivetrain
import org.team9432.subsystems.Shooter


object Robot: LoggedCoroutineRobot() {
    private val controller = XboxController(0)
    override fun robotPeriodic()  {
        KCommandScheduler.run()
        SmartDashboard.putNumber("bottomspeed", Shooter.getBottomVoltage())

    }

    override fun autonomousInit() {
        KCommandScheduler.schedule(auto())
    }

    override fun teleopInit() {
        Controls
    }

    override fun teleopPeriodic() {
        super.teleopPeriodic()
        Drivetrain.arcadeDrive(controller.getRawAxis(1), controller.getRawAxis(0))
    }

}