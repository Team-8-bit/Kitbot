package org.team9432.resources.intake

import org.team9432.annotation.Logged

interface IntakeIO {
    @Logged
    open class IntakeIOInputs {
        var backPositionRotations: Double = 0.0
        var backVelocityRPM: Double = 0.0
        var backAppliedVoltage: Double = 0.0
        var backSupplyCurrentAmps: Double = 0.0
        var backTempFahrenheit: Double = 0.0

        var frontPositionRotations: Double = 0.0
        var frontVelocityRPM: Double = 0.0
        var frontAppliedVoltage: Double = 0.0
        var frontSupplyCurrentAmps: Double = 0.0
        var frontTempFahrenheit: Double = 0.0
    }

    val backReduction get() = 26.0 / 36.0
    val frontReduction get() = 1.0

    fun setVoltage(volts: DoubleArray)

    fun updateInputs(inputs: IntakeIOInputs)
}