package org.team9432

import kotlinx.coroutines.delay
import org.team9432.resources.Drivetrain
import kotlin.time.Duration.Companion.seconds

object Auto {
    suspend fun shootAndDrive() {
        Actions.startShooting()
        delay(1.seconds)
        Actions.stopShooting()
        delay(1.seconds)
        Drivetrain.tankDrive(-0.5, -0.5)
        delay(3.seconds)
        Drivetrain.tankDrive(0.0, 0.0)
    }
    suspend fun onlyShoot() {
        Actions.startShooting()
        delay(1.seconds)
        Actions.stopShooting()
    }
}