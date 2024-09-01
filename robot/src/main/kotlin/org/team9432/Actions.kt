package org.team9432

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.team9432.lib.coroutines.CoroutineRobot
import org.team9432.lib.coroutines.RobotScope
import org.team9432.lib.coroutines.await
import org.team9432.oi.Controls
import org.team9432.resources.Intake
import org.team9432.resources.Loader
import org.team9432.resources.Shooter
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

object Actions {
    suspend fun intake() {
        Shooter.setState(Shooter.State.INTAKE)
        Loader.setState(Loader.State.INTAKE)
        Intake.setState(Intake.State.REVERSE)
        delay(0.25.seconds)
        Intake.beambreak.awaitTripped()
        Intake.beambreak.awaitClear()
        if (Robot.mode == CoroutineRobot.Mode.TELEOP) {
            RobotScope.launch { Controls.controller.rumbleDuration(1.seconds) }
        }
        Shooter.setState(Shooter.State.IDLE)
        Loader.setState(Loader.State.IDLE)
        Intake.setState(Intake.State.IDLE)
        Shooter.note = true
    }

    suspend fun groundIntake() {
        Intake.setState(Intake.State.INTAKE)
        delay(0.25.seconds)
        Intake.beambreak.awaitTripped()
        if (Robot.mode == CoroutineRobot.Mode.TELEOP) { RobotScope.launch { Controls.controller.rumbleDuration(1.seconds) }}
        Intake.setState(Intake.State.IDLE)
        Shooter.note = true
    }

    suspend fun drop() {
        Shooter.setState(Shooter.State.DROP)
        delay(250.milliseconds)
        Loader.setState(Loader.State.DROP)
        Intake.setState(Intake.State.LOAD)
        delay(1000.milliseconds)
        Loader.setState(Loader.State.IDLE)
        Shooter.setState(Shooter.State.IDLE)
        Intake.setState(Intake.State.IDLE)
        Shooter.note = false
    }


    fun startShooting() {
        Shooter.setState(Shooter.State.SHOOT)
    }

    suspend fun stopShooting() {
        Loader.setState(Loader.State.LOAD)
        Intake.setState(Intake.State.LOAD)
        delay(0.5.seconds)
        Loader.setState(Loader.State.IDLE)
        Shooter.setState(Shooter.State.IDLE)
        Intake.setState(Intake.State.IDLE)
        Shooter.note = false
    }

    fun idle() {
        Loader.setState(Loader.State.IDLE)
        Shooter.setState(Shooter.State.IDLE)
        Intake.setState(Intake.State.IDLE)
    }
}