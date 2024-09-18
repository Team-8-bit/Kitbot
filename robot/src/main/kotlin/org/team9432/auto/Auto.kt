package org.team9432.auto

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser
import org.team9432.lib.dashboard.AutoSelector

interface Auto {
    suspend fun run()

    fun addOptionToSelector(selector: SendableChooser<Auto>)
}