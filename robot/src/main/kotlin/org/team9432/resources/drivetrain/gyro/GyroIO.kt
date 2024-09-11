package org.team9432.resources.drivetrain.gyro

import edu.wpi.first.math.geometry.Rotation2d
import org.team9432.annotation.Logged

interface GyroIO {
    @Logged
    open class GyroIOInputs {
        var connected: Boolean = false
        var yawPosition: Rotation2d = Rotation2d()
        var yawVelocityRadPerSec: Double = 0.0
    }

    fun updateInputs(inputs: GyroIOInputs) {}
}