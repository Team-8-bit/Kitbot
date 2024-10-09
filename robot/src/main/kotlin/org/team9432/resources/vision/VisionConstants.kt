package org.team9432.resources.vision

import edu.wpi.first.math.Matrix
import edu.wpi.first.math.VecBuilder
import edu.wpi.first.math.geometry.Rotation3d
import edu.wpi.first.math.geometry.Transform3d
import edu.wpi.first.math.geometry.Translation3d
import edu.wpi.first.math.numbers.N1
import edu.wpi.first.math.numbers.N3
import edu.wpi.first.math.util.Units

object VisionConstants {
    val robotToCamera
        get() = Transform3d(
            Translation3d(
                Units.inchesToMeters(5.875),
                -Units.inchesToMeters(8.875),
                Units.inchesToMeters(8.5)
            ),
            Rotation3d(
                0.0,
                -Math.toRadians(28.125),
                Math.toRadians(6.920208)
            )
        )

    val singleTagStdDevs: Matrix<N3, N1> = VecBuilder.fill(0.25, 0.25, 999.0)
    val multiTagStdDevs: Matrix<N3, N1> = VecBuilder.fill(0.025, 0.025, 3.0)
    val maxStandardDeviations: Matrix<N3, N1> = VecBuilder.fill(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE)

    const val CAMERA_NAME = "Limelight" // It's not actually a limelight
}