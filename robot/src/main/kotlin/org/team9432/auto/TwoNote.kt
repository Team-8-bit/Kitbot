package org.team9432.auto

import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.kinematics.ChassisSpeeds
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser
import kotlinx.coroutines.delay
import org.team9432.Actions
import org.team9432.resources.drivetrain.Drivetrain
import kotlin.time.Duration.Companion.seconds

object TwoNote: Auto {
    override suspend fun run() {
        Actions.startShooting()
        delay(1.seconds)
        Actions.stopShooting()
        Drivetrain.runRawChassisSpeeds(ChassisSpeeds.fromRobotRelativeSpeeds(-1.0,0.0,0.0, Rotation2d.fromRotations(0.0)))
        Actions.groundIntake()
        Drivetrain.runRawChassisSpeeds(ChassisSpeeds.fromRobotRelativeSpeeds(1.0,0.0,0.0, Rotation2d.fromRotations(0.0)))
        delay(1.625.seconds)
        Drivetrain.runRawChassisSpeeds(ChassisSpeeds.fromRobotRelativeSpeeds(0.0,0.0,0.0, Rotation2d.fromRotations(0.0)))
        Actions.startShooting()
        delay(1.seconds)
        Actions.stopShooting()
    }

    override fun addOptionToSelector(selector: SendableChooser<Auto>) {
        selector.addOption("2 Note",this)
    }
}