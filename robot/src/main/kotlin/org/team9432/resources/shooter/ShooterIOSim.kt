package org.team9432.resources.shooter

import edu.wpi.first.math.MathUtil
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.controller.SimpleMotorFeedforward
import edu.wpi.first.math.system.plant.DCMotor
import edu.wpi.first.wpilibj.simulation.DCMotorSim
import org.team9432.Robot

class ShooterIOSim: ShooterIO {
    private val leftSim = DCMotorSim(DCMotor.getNEO(1), backReduction, 0.001)
    private val rightSim = DCMotorSim(DCMotor.getNEO(1), frontReduction, 0.001)

    private val leftPid = PIDController(0.0039231, 0.0, 0.0)
    private val rightPid = PIDController(0.0039231, 0.0, 0.0)
    private var ff = SimpleMotorFeedforward(0.0, 0.0086634, 0.0038234)//TODO Tune feedforward

    private var leftAppliedVoltage = 0.0
    private var rightAppliedVoltage = 0.0



    override fun setSpeeds(speeds: DoubleArray) {
        leftAppliedVoltage = MathUtil.clamp(leftPid.calculate(speeds[0])/2 + ff.calculate(leftPid.setpoint), -12.0, 12.0)
        leftSim.setInputVoltage(leftAppliedVoltage)

        rightAppliedVoltage = MathUtil.clamp(rightPid.calculate(speeds[1])/2 + ff.calculate(rightPid.setpoint), -12.0, 12.0)
        rightSim.setInputVoltage(rightAppliedVoltage)
    }

    override fun updateInputs(inputs: ShooterIO.ShooterIOInputs) {
        leftSim.update(Robot.period)
        inputs.leftPositionRotations = leftSim.angularPositionRotations
        inputs.leftVelocityRPM = leftSim.angularVelocityRPM
        inputs.leftAppliedVoltage = leftAppliedVoltage
        inputs.leftSupplyCurrentAmps = leftSim.currentDrawAmps

        rightSim.update(Robot.period)
        inputs.rightPositionRotations = rightSim.angularPositionRotations
        inputs.rightVelocityRPM = rightSim.angularVelocityRPM
        inputs.rightAppliedVoltage = rightAppliedVoltage
        inputs.rightSupplyCurrentAmps = rightSim.currentDrawAmps
    }
}