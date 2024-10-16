package org.team9432.resources.vision

import edu.wpi.first.math.Matrix
import edu.wpi.first.math.VecBuilder
import edu.wpi.first.math.geometry.Pose3d
import edu.wpi.first.math.numbers.N1
import edu.wpi.first.math.numbers.N3
import org.littletonrobotics.junction.Logger
import org.photonvision.EstimatedRobotPose
import org.photonvision.PhotonPoseEstimator
import org.photonvision.targeting.PhotonPipelineResult
import org.team9432.FieldConstants.apriltagFieldLayout
import org.team9432.lib.RobotPeriodicManager
import org.team9432.lib.constants.EvergreenFieldConstants.isOnField
import org.team9432.lib.util.simSwitch
import org.team9432.resources.drivetrain.Drivetrain
import kotlin.jvm.optionals.getOrNull
import kotlin.math.abs

object Vision {
    private val io = simSwitch(real = { VisionIOReal() }, sim = { VisionIOSim() })
    private val inputs = LoggedVisionIOInputs()

    val isEnabled get() = inputs.isConnected

    private val poseEstimator = PhotonPoseEstimator(
        apriltagFieldLayout,
        PhotonPoseEstimator.PoseStrategy.MULTI_TAG_PNP_ON_COPROCESSOR,
        VisionConstants.robotToCamera
    )

    init {
        poseEstimator.setMultiTagFallbackStrategy(PhotonPoseEstimator.PoseStrategy.LOWEST_AMBIGUITY)
        RobotPeriodicManager.startPeriodic { periodic() }
    }

    private fun periodic() {
        io.updateInputs(inputs)
        //Logger.processInputs("Vision", inputs)

        Logger.recordOutput("Vision/Connected", inputs.isConnected)
        Logger.recordOutput("Vision/Enabled", isEnabled)
        Logger.recordOutput("Vision/TrackedTagIds", *inputs.results.targets.mapNotNull { apriltagFieldLayout.getTagPose(it.fiducialId).getOrNull() }.toTypedArray())

        applyToPoseEstimator(inputs.results)
    }
    val tagStdDevs: Matrix<N3, N1> = VecBuilder.fill(0.4, 0.4, 1.0)

    private fun applyToPoseEstimator(result: PhotonPipelineResult) {
        // Get the estimated position or return
        val estimatedRobotPose = poseEstimator.update(result).getOrNull() ?: return

        // Make sure the estimation is valid, i.e. not in the floor or outside the field
        if (!estimatedRobotPose.estimatedPose.isValid()) return

        val visionPose = estimatedRobotPose.estimatedPose.toPose2d()
        val timestamp = estimatedRobotPose.timestampSeconds
        val stdDevs: Matrix<N3, N1> = getEstimationStdDevs(estimatedRobotPose)

        Logger.recordOutput("Vision/3dPose",estimatedRobotPose.estimatedPose)

        Drivetrain.addVisionMeasurement(visionPose, timestamp, stdDevs)
    }

    private fun getEstimationStdDevs(estimatedPose: EstimatedRobotPose): Matrix<N3, N1> {
        // A count of the number of valid tags seen
        var numTags = 0

        // Get the average distance of apriltags that the camera sees
        val avgDist = estimatedPose.targetsUsed.map { target ->
            val tagPose = apriltagFieldLayout.getTagPose(target.fiducialId)
            if (tagPose.isEmpty) return@map null
            numTags++

            val estimatedPose2d = estimatedPose.estimatedPose.toPose2d()
            tagPose.get().toPose2d().translation.getDistance(estimatedPose2d.translation)
        }.filterNotNull().average()

        var deviation = tagStdDevs.times(avgDist * 0.5)
        if (estimatedPose.targetsUsed.size == 1) {
            deviation = deviation.times(2.0)
        }

            return deviation
//        when {
//            numTags == 0 -> return VisionConstants.singleTagStdDevs
//            numTags == 1 -> {
//                if (avgDist > 5) VisionConstants.maxStandardDeviations
//                else VisionConstants.singleTagStdDevs.times(1 + (avgDist * avgDist) / 30)
//            }
//
//            numTags > 1 -> {
//                if (avgDist > 7) VisionConstants.maxStandardDeviations
//                else VisionConstants.multiTagStdDevs
//            }
//
//            else -> VisionConstants.maxStandardDeviations
//        }
    }

    /** Check that the given position is close to the floor and within the field walls. */
    private fun Pose3d.isValid() = abs(z) < 0.25 && this.isOnField()
}