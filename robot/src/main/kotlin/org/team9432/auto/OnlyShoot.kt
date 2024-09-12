package org.team9432.auto

import kotlinx.coroutines.delay
import org.team9432.Actions
import org.team9432.lib.dashboard.AutoSelector
import kotlin.time.Duration.Companion.seconds

object OnlyShoot: Auto {
    override suspend fun run() {
        Actions.startShooting()
        delay(1.seconds)
        Actions.stopShooting()
    }

    override fun addOptionToSelector(selector: AutoSelector.AutoSelectorOptionScope<Auto>) = selector.apply {
        addOption("Only Shoot") { OnlyShoot }
    }
}