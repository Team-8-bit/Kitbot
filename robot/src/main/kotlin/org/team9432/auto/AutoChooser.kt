package org.team9432.auto

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.team9432.Robot
import org.team9432.lib.dashboard.AutoSelector
import kotlin.time.Duration.Companion.seconds

object AutoChooser {
    private const val CHOOSER_COUNT = 1
    private val choosers = List(CHOOSER_COUNT) { AutoSelector.DashboardQuestion("Option $it Chooser", "Option $it Question") }.toSet()

    private var currentlySelectedAuto: Auto? = null

    private val chooser = AutoSelector(choosers) {
        addQuestion("Which Auto?", { currentlySelectedAuto = it }) {
            OnlyShoot.addOptionToSelector(this)
        }
    }

    fun getAuto() = currentlySelectedAuto

    init {
        Robot.coroutineScope.launch {
            while (true) {
                chooser.update()
                delay(0.25.seconds)
            }
        }
    }
}