package org.team9432.resources.drivetrain.module

import edu.wpi.first.math.MathUtil
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.system.plant.DCMotor
import edu.wpi.first.wpilibj.simulation.DCMotorSim
import org.team9432.resources.drivetrain.module.ModuleIO.ModuleIOInputs
import kotlin.math.abs

/**
 * Physics sim implementation of module IO.
 *
 *
 * Uses two flywheel sims for the drive and turn motors, with the absolute position initialized
 * to a random value. The flywheel sims are not physically accurate, but provide a decent
 * approximation for the behavior of the module.
 */
class ModuleIOSim: ModuleIO {
    private val driveSim = DCMotorSim(DCMotor.getNEO(1), 6.75, 0.025)
    private val turnSim = DCMotorSim(DCMotor.getNEO(1), 150.0 / 7.0, 0.004)

    private val turnAbsoluteInitPosition = Rotation2d(Math.random() * 2.0 * Math.PI)
    private var driveAppliedVolts = 0.0
    private var turnAppliedVolts = 0.0

    override fun updateInputs(inputs: ModuleIOInputs) {
        driveSim.update(LOOP_PERIOD_SECS)
        turnSim.update(LOOP_PERIOD_SECS)

        inputs.drivePositionRad = driveSim.angularPositionRad
        inputs.driveVelocityRadPerSec = driveSim.angularVelocityRadPerSec
        inputs.driveAppliedVolts = driveAppliedVolts
        inputs.driveCurrentAmps = abs(driveSim.currentDrawAmps)

        inputs.turnAbsolutePosition = Rotation2d(turnSim.angularPositionRad).plus(turnAbsoluteInitPosition)
        inputs.turnPosition = Rotation2d(turnSim.angularPositionRad)
        inputs.turnVelocityRadPerSec = turnSim.angularVelocityRadPerSec
        inputs.turnAppliedVolts = turnAppliedVolts
        inputs.turnCurrentAmps = abs(turnSim.currentDrawAmps)
    }

    override fun setDriveVoltage(volts: Double) {
        driveAppliedVolts = MathUtil.clamp(volts, -12.0, 12.0)
        driveSim.setInputVoltage(driveAppliedVolts)
    }

    override fun setTurnVoltage(volts: Double) {
        turnAppliedVolts = MathUtil.clamp(volts, -12.0, 12.0)
        turnSim.setInputVoltage(turnAppliedVolts)
    }

    companion object {
        private const val LOOP_PERIOD_SECS = 0.02
    }
}