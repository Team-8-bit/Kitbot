package org.team9432.resources.loader

import org.team9432.annotation.Logged

interface LoaderIO {
    @Logged
    open class LoaderIOInputs {
        var positionRotations: Double = 0.0
        var velocityRPM: Double = 0.0
        var appliedVoltage: Double = 0.0
        var supplyCurrentAmps: Double = 0.0
        var tempFahrenheit: Double = 0.0
    }

    fun setSpeed(speed: Double)

    fun updateInputs(inputs: LoaderIOInputs)
}