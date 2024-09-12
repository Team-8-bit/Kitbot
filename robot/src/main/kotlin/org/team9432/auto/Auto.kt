package org.team9432.auto

import org.team9432.lib.dashboard.AutoSelector

interface Auto {
    suspend fun run()

    fun addOptionToSelector(selector: AutoSelector.AutoSelectorOptionScope<Auto>): AutoSelector.AutoSelectorOptionScope<Auto>
}