package org.team9432.resources.loader

import edu.wpi.first.math.MathUtil
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.controller.SimpleMotorFeedforward
import edu.wpi.first.math.system.plant.DCMotor
import edu.wpi.first.wpilibj.simulation.DCMotorSim
import org.team9432.Robot

class LoaderIOSim: LoaderIO {
    private val sim = DCMotorSim(DCMotor.getNEO(1), 1.0, 0.001)
    private var appliedVoltage = 0.0

    private val pid = PIDController(0.0039231, 0.0, 0.0)
    private var ff = SimpleMotorFeedforward(0.0, 0.0086634, 0.0038234)


    override fun setSpeed(speed: Double) {
        appliedVoltage = MathUtil.clamp(pid.calculate(speed) / 2 + ff.calculate(speed), -12.0, 12.0)
        sim.setInputVoltage(appliedVoltage)
    }

    override fun updateInputs(inputs: LoaderIO.LoaderIOInputs) {
        sim.update(Robot.period)
        inputs.positionRotations = sim.angularPositionRotations
        inputs.velocityRPM = sim.angularVelocityRPM
        inputs.appliedVoltage = appliedVoltage
        inputs.supplyCurrentAmps = sim.currentDrawAmps
    }
}