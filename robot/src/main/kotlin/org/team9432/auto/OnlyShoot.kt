package org.team9432.auto

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser
import kotlinx.coroutines.delay
import org.team9432.Actions
import kotlin.time.Duration.Companion.seconds

object OnlyShoot: Auto {
    override suspend fun run() {
        Actions.startShooting()
        delay(1.seconds)
        Actions.stopShooting()
    }

    override fun addOptionToSelector(selector: SendableChooser<Auto>) {
        selector.addOption("Only Shoot",this)
    }
}