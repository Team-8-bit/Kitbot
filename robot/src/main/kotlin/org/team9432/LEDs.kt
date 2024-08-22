package org.team9432

import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.RobotController
import kotlinx.coroutines.launch
import org.team9432.lib.RobotPeriodicManager
import org.team9432.lib.coroutines.CoroutineRobot
import org.team9432.lib.coroutines.RobotScope
import org.team9432.lib.led.animations.*
import org.team9432.lib.led.color.Color
import org.team9432.lib.led.color.predefined.*
import org.team9432.lib.led.management.AnimationBindScope
import org.team9432.lib.led.management.AnimationManager
import org.team9432.lib.led.management.Section
import org.team9432.lib.led.strip.LEDStrip
import org.team9432.lib.led.strip.RioLedStrip
import org.team9432.resources.Shooter
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

object LEDs {
    init {
        LEDStrip.create(RioLedStrip(22, 0))

        RobotScope.launch {
            AnimationManager.run(20.milliseconds)
        }


        val leds = Section((0..21).toSet())

        val scope = AnimationBindScope.build {
            If({ Robot.mode == CoroutineRobot.Mode.DISABLED }) {
                setAnimation {
                    repeat(9999) {
                        leds.breath(Color.ForestColors).invoke()
                    }
                }
            }.ElseIf({ Robot.mode == CoroutineRobot.Mode.AUTONOMOUS }) {
                setAnimation(leds.strobe(Color.Red, period = 0.5.seconds))
            }.ElseIf({ Robot.mode == CoroutineRobot.Mode.TELEOP }) {
                If({ Shooter.shooterState == Shooter.State.INTAKE }) {
                    setAnimation(leds.strobe(Color.Green, 250.milliseconds))
                }.ElseIf({ Shooter.note }) {
                    setAnimation(leds.strobe(Color.DarkOrange, 250.milliseconds))
                }.Else {
                    If({ Robot.alliance == DriverStation.Alliance.Red }) {
                        setAnimation(leds.solid(Color.Red))
                    }.ElseIf({ Robot.alliance == DriverStation.Alliance.Blue }) {
                        setAnimation(leds.solid(Color.Blue))
                    }
                }
            }
        }

        RobotPeriodicManager.startPeriodic {
            scope.update()
        }
    }
}