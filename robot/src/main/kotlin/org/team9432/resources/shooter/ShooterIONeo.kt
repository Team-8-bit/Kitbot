package org.team9432.resources.shooter

import com.revrobotics.CANSparkBase
import com.revrobotics.CANSparkLowLevel
import com.revrobotics.CANSparkMax
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.controller.SimpleMotorFeedforward
import org.team9432.lib.util.temperatureFahrenheit

class ShooterIONeo: ShooterIO {
    private val motorLeft = CANSparkMax(22, CANSparkLowLevel.MotorType.kBrushless)
    private val motorRight = CANSparkMax(23, CANSparkLowLevel.MotorType.kBrushless)

    private val encoderBack = motorLeft.encoder
    private val encoderFront = motorRight.encoder

    private val leftPid = PIDController(0.0039231, 0.0, 0.0)
    private val rightPid = PIDController(0.0039231, 0.0, 0.0)
    private var ff = SimpleMotorFeedforward(0.0, 0.0086634, 0.0038234)//TODO Tune feedforward

    init {
        motorLeft.inverted = true
        motorLeft.enableVoltageCompensation(12.0)
        motorLeft.setSmartCurrentLimit(70)
        motorLeft.idleMode = CANSparkBase.IdleMode.kCoast
        motorLeft.burnFlash()

        motorRight.inverted = false
        motorRight.enableVoltageCompensation(12.0)
        motorRight.setSmartCurrentLimit(70)
        motorRight.idleMode = CANSparkBase.IdleMode.kCoast
        motorRight.burnFlash()
    }

    override fun setSpeeds(speeds: DoubleArray) {
        motorLeft.setVoltage(leftPid.calculate(speeds[0])/2 + ff.calculate(leftPid.setpoint))
        motorRight.setVoltage(rightPid.calculate(speeds[1])/2 + ff.calculate(rightPid.setpoint))
    }

    override fun updateInputs(inputs: ShooterIO.ShooterIOInputs) {
        inputs.leftPositionRotations = encoderBack.position / backReduction
        inputs.leftVelocityRPM = encoderBack.velocity / backReduction
        inputs.leftAppliedVoltage = motorLeft.appliedOutput * motorLeft.busVoltage
        inputs.leftSupplyCurrentAmps = motorLeft.outputCurrent
        inputs.leftTempFahrenheit = motorLeft.temperatureFahrenheit

        inputs.rightPositionRotations = encoderFront.position / frontReduction
        inputs.rightVelocityRPM = encoderFront.velocity / frontReduction
        inputs.rightAppliedVoltage = motorRight.appliedOutput * motorRight.busVoltage
        inputs.rightSupplyCurrentAmps = motorRight.outputCurrent
        inputs.rightTempFahrenheit = motorRight.temperatureFahrenheit
    }
}