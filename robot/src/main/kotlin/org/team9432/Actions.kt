package org.team9432

import kotlinx.coroutines.delay
import org.team9432.lib.coroutines.await
import org.team9432.lib.resource.use
import org.team9432.resources.Loader
import org.team9432.resources.Shooter
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

object Actions {
    suspend fun intake() {
        use(Shooter, Loader) {
            Shooter.setState(Shooter.State.INTAKE)
            Loader.setState(Loader.State.INTAKE)
            await(20.milliseconds) { Loader.motorBottom.outputCurrent > 8 }
            Shooter.setState(Shooter.State.IDLE)
            Loader.setState(Loader.State.IDLE)
        }
    }

    suspend fun stopIntaking() {
        use(Shooter, Loader) {
            Shooter.setState(Shooter.State.IDLE)
            Loader.setState(Loader.State.IDLE)
        }
    }

    suspend fun startShooting() {
        use(Shooter, Loader) {
            Shooter.setState(Shooter.State.SHOOT)
        }
    }

    suspend fun stopShooting() {
        use(Shooter, Loader) {
            Loader.setState(Loader.State.LOAD)
            delay(0.5.seconds)
            Loader.setState(Loader.State.IDLE)
            Shooter.setState(Shooter.State.IDLE)
        }
    }

}