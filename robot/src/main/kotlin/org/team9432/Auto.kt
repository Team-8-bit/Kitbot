package org.team9432

import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

object Auto {
    suspend fun ShootAndDrive() {
        Actions.startShooting()
        delay(1.seconds)
        Actions.stopShooting()
    }
    suspend fun OnlyShoot() {
        Actions.startShooting()
        delay(1.seconds)
        Actions.stopShooting()
    }
}