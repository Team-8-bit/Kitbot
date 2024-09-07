package org.team9432.resources.shooter

import org.team9432.annotation.Logged

interface ShooterIO {
    @Logged
    open class ShooterIOInputs {
        var leftPositionRotations: Double = 0.0
        var leftVelocityRPM: Double = 0.0
        var leftAppliedVoltage: Double = 0.0
        var leftSupplyCurrentAmps: Double = 0.0
        var leftTempFahrenheit: Double = 0.0

        var rightPositionRotations: Double = 0.0
        var rightVelocityRPM: Double = 0.0
        var rightAppliedVoltage: Double = 0.0
        var rightSupplyCurrentAmps: Double = 0.0
        var rightTempFahrenheit: Double = 0.0

    }

    val backReduction get() = 1.0
    val frontReduction get() = 1.0

    fun setSpeeds(speeds: DoubleArray)

    fun updateInputs(inputs: ShooterIOInputs)
}