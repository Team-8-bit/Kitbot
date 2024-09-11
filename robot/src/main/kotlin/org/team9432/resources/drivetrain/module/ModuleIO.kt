package org.team9432.resources.drivetrain.module

import edu.wpi.first.math.geometry.Rotation2d
import org.team9432.annotation.Logged

interface ModuleIO {
    @Logged
    open class ModuleIOInputs {
        var drivePositionRad: Double = 0.0
        var driveVelocityRadPerSec: Double = 0.0
        var driveAppliedVolts: Double = 0.0
        var driveCurrentAmps: Double = 0.0

        var turnAbsolutePosition: Rotation2d = Rotation2d()
        var turnPosition: Rotation2d = Rotation2d()
        var turnVelocityRadPerSec: Double = 0.0
        var turnAppliedVolts: Double = 0.0
        var turnCurrentAmps: Double = 0.0
    }

    /** Updates the set of loggable inputs.  */
    fun updateInputs(inputs: ModuleIOInputs) {}

    /** Run the drive motor at the specified voltage.  */
    fun setDriveVoltage(volts: Double) {}

    /** Run the turn motor at the specified voltage.  */
    fun setTurnVoltage(volts: Double) {}

    enum class Module {
        FRONT_LEFT, FRONT_RIGHT, BACK_LEFT, BACK_RIGHT
    }
}