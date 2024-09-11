package org.team9432

import kotlinx.coroutines.delay
import org.team9432.lib.coroutines.await
import org.team9432.lib.coroutines.parallel
import org.team9432.resources.Drivetrain
import org.team9432.resources.intake.Intake
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

object Auto {
    suspend fun shootAndDrive() {
        Actions.startShooting()
        delay(1.seconds)
        Actions.stopShooting()
        delay(1.seconds)
        Drivetrain.tankDrive(0.5, 0.5)
        delay(3.seconds)
        Drivetrain.tankDrive(0.0, 0.0)
    }

    suspend fun onlyShoot() {
        Actions.startShooting()
        delay(1.seconds)
        Actions.stopShooting()
    }

    suspend fun basicTwoNote() { //TODO TUNE
        Actions.startShooting()
        delay(1.5.seconds)
        Actions.stopShooting()
        parallel(
            Actions::groundIntake,
            { Drivetrain.tankDrive(0.375, 0.375) }
        )
        Intake.beambreak.awaitTripped(20.milliseconds)
        Drivetrain.tankDrive(-0.375, -0.375)
        delay(1.seconds)
        await { Drivetrain.rightBottomDriveMotor.outputCurrent > 15.0 }
        Drivetrain.tankDrive(0.0, 0.0)
        Actions.startShooting()
        delay(1.5.seconds)
        Actions.stopShooting()
    }
}