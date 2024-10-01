package org.team9432.resources.intake

import com.revrobotics.CANSparkBase
import com.revrobotics.CANSparkLowLevel
import com.revrobotics.CANSparkMax
import org.team9432.lib.util.temperatureFahrenheit

class IntakeIONeo: IntakeIO {
    private val motorBack = CANSparkMax(18, CANSparkLowLevel.MotorType.kBrushless)
    private val motorFront = CANSparkMax(16, CANSparkLowLevel.MotorType.kBrushless)

    private val encoderBack = motorBack.encoder
    private val encoderFront = motorFront.encoder

    init {
        motorBack.inverted = false
        motorBack.enableVoltageCompensation(11.0)
        motorBack.setSmartCurrentLimit(40)
        motorBack.idleMode = CANSparkBase.IdleMode.kBrake
        motorBack.burnFlash()

        motorFront.inverted = true
        motorFront.enableVoltageCompensation(11.0)
        motorFront.setSmartCurrentLimit(30)
        motorFront.idleMode = CANSparkBase.IdleMode.kBrake
        motorFront.burnFlash()
    }

    override fun setVoltage(volts: DoubleArray) {
        motorBack.setVoltage(volts[0])
        motorFront.setVoltage(volts[1])
    }

    override fun updateInputs(inputs: IntakeIO.IntakeIOInputs) {
        inputs.backPositionRotations = encoderBack.position / backReduction
        inputs.backVelocityRPM = encoderBack.velocity / backReduction
        inputs.backAppliedVoltage = motorBack.appliedOutput * motorBack.busVoltage
        inputs.backSupplyCurrentAmps = motorBack.outputCurrent
        inputs.backTempFahrenheit = motorBack.temperatureFahrenheit

        inputs.frontPositionRotations = encoderFront.position / frontReduction
        inputs.frontVelocityRPM = encoderFront.velocity / frontReduction
        inputs.frontAppliedVoltage = motorFront.appliedOutput * motorFront.busVoltage
        inputs.frontSupplyCurrentAmps = motorFront.outputCurrent
        inputs.frontTempFahrenheit = motorFront.temperatureFahrenheit
    }
}