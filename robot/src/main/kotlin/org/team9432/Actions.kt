package org.team9432

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.team9432.lib.coroutines.CoroutineRobot
import org.team9432.lib.coroutines.RobotScope
import org.team9432.lib.coroutines.await
import org.team9432.lib.resource.use
import org.team9432.oi.Controls
import org.team9432.resources.Loader
import org.team9432.resources.Shooter
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

object Actions {
    suspend fun intake() {
        Shooter.setState(Shooter.State.INTAKE)
        Loader.setState(Loader.State.INTAKE)
        await(20.milliseconds) { Loader.motorBottom.outputCurrent > 8 }
        if (Robot.mode == CoroutineRobot.Mode.TELEOP) {
            RobotScope.launch { Controls.controller.rumbleDuration(2.seconds) }
        }
        Shooter.setState(Shooter.State.IDLE)
        Loader.setState(Loader.State.IDLE)
    }

    suspend fun drop() {
        Shooter.setState(Shooter.State.DROP)
        delay(0.25.seconds)
        Loader.setState(Loader.State.LOAD)
        delay(0.25.seconds)
        Loader.setState(Loader.State.IDLE)
        Shooter.setState(Shooter.State.IDLE)
    }

    fun stopIntaking() {
        Shooter.setState(Shooter.State.IDLE)
        Loader.setState(Loader.State.IDLE)
    }

    fun startShooting() {
        Shooter.setState(Shooter.State.SHOOT)
    }

    suspend fun stopShooting() {
        Loader.setState(Loader.State.LOAD)
        delay(0.5.seconds)
        Loader.setState(Loader.State.IDLE)
        Shooter.setState(Shooter.State.IDLE)
    }

}