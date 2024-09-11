package org.team9432.resources.loader

import com.revrobotics.CANSparkBase
import com.revrobotics.CANSparkLowLevel
import com.revrobotics.CANSparkMax
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.controller.SimpleMotorFeedforward
import org.team9432.lib.util.temperatureFahrenheit

class LoaderIONeo: LoaderIO {
    private val motor = CANSparkMax(21, CANSparkLowLevel.MotorType.kBrushless)
    private val encoder = motor.encoder

    private val bottomPid = PIDController(0.0039231, 0.0, 0.0)
    private var feedforward = SimpleMotorFeedforward(0.0, 0.0086634, 0.0038234)

    init {
        motor.inverted = false
        motor.enableVoltageCompensation(11.0)
        motor.setSmartCurrentLimit(60)
        motor.idleMode = CANSparkBase.IdleMode.kCoast
        motor.burnFlash()
    }

    override fun setSpeed(speed: Double) {
        motor.setVoltage(bottomPid.calculate(speed) / 2 + feedforward.calculate(speed))
    }

    override fun updateInputs(inputs: LoaderIO.LoaderIOInputs) {
        inputs.positionRotations = encoder.position
        inputs.velocityRPM = encoder.velocity
        inputs.appliedVoltage = motor.appliedOutput * motor.busVoltage
        inputs.supplyCurrentAmps = motor.outputCurrent
        inputs.tempFahrenheit = motor.temperatureFahrenheit
    }
}