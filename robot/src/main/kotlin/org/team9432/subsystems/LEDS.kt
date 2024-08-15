package org.team9432.subsystems

import edu.wpi.first.hal.AllianceStationID
import edu.wpi.first.wpilibj.DriverStation
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.team9432.Robot
import org.team9432.lib.commandbased.KPeriodic
import org.team9432.lib.commandbased.KSubsystem
import org.team9432.lib.ifRedElse
import org.team9432.lib.led.animations.breath
import org.team9432.lib.led.animations.solid
import org.team9432.lib.led.animations.strobe
import org.team9432.lib.led.color.Color
import org.team9432.lib.led.color.predefined.*
import org.team9432.lib.led.management.Animation
import org.team9432.lib.led.management.AnimationBindScope
import org.team9432.lib.led.management.AnimationManager
import org.team9432.lib.led.management.Section
import org.team9432.lib.led.strip.LEDStrip
import org.team9432.lib.led.strip.RioLedStrip
import org.team9432.lib.unit.milliseconds
import kotlin.jvm.optionals.getOrNull

object LEDS : KPeriodic() {
    private val scope:AnimationBindScope
    init {
        LEDStrip.create(RioLedStrip(22,0))

        AnimationManager

        val leds = Section((0..21).toSet())


        scope = AnimationBindScope.build {
            If ({ DriverStation.getAlliance().getOrNull() == DriverStation.Alliance.Red }){
                setAnimation (leds.strobe(Color.Red, 250.milliseconds))
            }.ElseIf({ DriverStation.getAlliance().getOrNull() == DriverStation.Alliance.Blue }){
                setAnimation (leds.strobe(Color.Aqua, 250.milliseconds))

        }

        }

    }

    override fun periodic() {
        scope.update()
    }
}