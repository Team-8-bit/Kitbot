package org.team9432

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.team9432.Robot.coroutineScope
import org.team9432.oi.Controls
import org.team9432.resources.intake.Intake
import org.team9432.resources.loader.Loader
import org.team9432.resources.shooter.Shooter
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
        if (Robot.mode.isTeleop) {
            coroutineScope.launch { Controls.controller.rumbleDuration(1.seconds) }
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
        if (Robot.mode.isTeleop) {
            coroutineScope.launch { Controls.controller.rumbleDuration(1.seconds) }
        }
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
        Loader.setState(Loader.State.SPEED)
    }

    suspend fun stopShooting() {
        Intake.setState(Intake.State.LOAD)
        Intake.beambreak.awaitClear()
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