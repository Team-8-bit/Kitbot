package org.team9432.resources.intake

import edu.wpi.first.math.MathUtil
import edu.wpi.first.math.system.plant.DCMotor
import edu.wpi.first.wpilibj.simulation.DCMotorSim
import org.team9432.Robot

class IntakeIOSim: IntakeIO {
    private val backSim = DCMotorSim(DCMotor.getNEO(1), backReduction, 0.001)
    private val frontSim = DCMotorSim(DCMotor.getNEO(1), frontReduction, 0.001)

    private var backAppliedVoltage = 0.0
    private var frontAppliedVoltage = 0.0


    override fun setVoltage(volts: DoubleArray) {
        backAppliedVoltage = MathUtil.clamp(volts[0], -12.0, 12.0)
        backSim.setInputVoltage(backAppliedVoltage)

        frontAppliedVoltage = MathUtil.clamp(volts[1], -12.0, 12.0)
        frontSim.setInputVoltage(frontAppliedVoltage)
    }

    override fun updateInputs(inputs: IntakeIO.IntakeIOInputs) {
        backSim.update(Robot.period)
        inputs.backPositionRotations = backSim.angularPositionRotations
        inputs.backVelocityRPM = backSim.angularVelocityRPM
        inputs.backAppliedVoltage = backAppliedVoltage
        inputs.backSupplyCurrentAmps = backSim.currentDrawAmps

        frontSim.update(Robot.period)
        inputs.frontPositionRotations = frontSim.angularPositionRotations
        inputs.frontVelocityRPM = frontSim.angularVelocityRPM
        inputs.frontAppliedVoltage = frontAppliedVoltage
        inputs.frontSupplyCurrentAmps = frontSim.currentDrawAmps
    }
}