package org.team9432.auto

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.team9432.Robot
import kotlin.time.Duration.Companion.seconds

object AutoChooser {
    private val Selector = SendableChooser<Auto>()
    private var currentlySelectedAuto: Auto? = null

    fun getAuto() = currentlySelectedAuto

    init {
        Robot.coroutineScope.launch {
            while (true) {
                SmartDashboard.putData("Which Auto?",Selector)
                currentlySelectedAuto = Selector.selected
                delay(0.25.seconds)
            }
        }
        OnlyShoot.addOptionToSelector(Selector)
        ShootDrive.addOptionToSelector(Selector)
    }
}