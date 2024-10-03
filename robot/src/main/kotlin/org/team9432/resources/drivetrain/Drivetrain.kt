package org.team9432.resources.drivetrain

import com.pathplanner.lib.auto.AutoBuilder
import com.pathplanner.lib.util.HolonomicPathFollowerConfig
import com.pathplanner.lib.util.PIDConstants
import com.pathplanner.lib.util.PathPlannerLogging
import com.pathplanner.lib.util.ReplanningConfig
import edu.wpi.first.math.VecBuilder
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator
import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.geometry.Twist2d
import edu.wpi.first.math.kinematics.ChassisSpeeds
import edu.wpi.first.math.kinematics.SwerveDriveKinematics
import edu.wpi.first.math.kinematics.SwerveModulePosition
import edu.wpi.first.math.kinematics.SwerveModuleState
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.DriverStation.Alliance
import edu.wpi.first.wpilibj2.command.Subsystem
import org.littletonrobotics.junction.AutoLogOutput
import org.littletonrobotics.junction.Logger
import org.team9432.LimelightHelpers
import org.team9432.lib.RobotPeriodicManager
import org.team9432.lib.util.SwerveUtil
import org.team9432.lib.util.simSwitch
import org.team9432.resources.drivetrain.gyro.GyroIO
import org.team9432.resources.drivetrain.gyro.GyroIOPigeon2
import org.team9432.resources.drivetrain.gyro.LoggedGyroIOInputs
import org.team9432.resources.drivetrain.module.Module
import org.team9432.resources.drivetrain.module.ModuleIO
import org.team9432.resources.drivetrain.module.ModuleIONeo
import org.team9432.resources.drivetrain.module.ModuleIOSim


object Drivetrain {
    private val gyroInputs: LoggedGyroIOInputs = LoggedGyroIOInputs()
    private val gyroIO: GyroIO = simSwitch(real = GyroIOPigeon2(), sim = object: GyroIO {})
    private val moduleTranslations = SwerveUtil.getMk4iModuleTranslations(26.0)
    const val MAX_VELOCITY_MPS = 4.5
    val MAX_ANG_VELOCITY_RAD_PER_SEC = Math.toRadians(360.0)

    private val modules: Array<Module> =
        simSwitch(
            real = ModuleIO.Module.entries.mapIndexed { index, moduleInfo -> Module(ModuleIONeo(moduleInfo), index) }.toTypedArray(),
            sim = Array(4) { index -> Module(ModuleIOSim(), index) }
        )

    private val kinematics: SwerveDriveKinematics = SwerveDriveKinematics(*moduleTranslations)

    private var rawGyroRotation: Rotation2d = Rotation2d()

    // For delta tracking
    private val lastModulePositions: Array<SwerveModulePosition> = Array(4) { SwerveModulePosition() }

    private val poseEstimator: SwerveDrivePoseEstimator = SwerveDrivePoseEstimator(kinematics, rawGyroRotation, lastModulePositions, Pose2d())

    private val subsystem: Subsystem = object : Subsystem {}

    init {

        // Configure AutoBuilder for PathPlanner
        AutoBuilder.configureHolonomic(
            this::getPose,  // Robot pose supplier
            this::setPose,  // Method to reset odometry (will be called if your auto has a starting pose)
            { kinematics.toChassisSpeeds(*getModuleStates()) },  // ChassisSpeeds supplier. MUST BE ROBOT RELATIVE
            this::runRawChassisSpeeds,  // Method that will drive the robot given ROBOT RELATIVE ChassisSpeeds
            HolonomicPathFollowerConfig( // HolonomicPathFollowerConfig, this should likely live in your Constants class
                PIDConstants(4.0, 0.0, 0.0),  // Translation PID constants
                PIDConstants(4.0, 0.0, 0.0),  // Rotation PID constants
                4.0,  // Max module speed, in m/s
                0.3727,  // Drive base radius in meters. Distance from robot center to furthest module. // full number 0.3726806290243699
                ReplanningConfig() // Default path replanning config. See the API for the options here
            ),
            {
                // Boolean supplier that controls when the path will be mirrored for the red alliance
                // This will flip the path being followed to the red side of the field.
                // THE ORIGIN WILL REMAIN ON THE BLUE SIDE
                val alliance = DriverStation.getAlliance()
                if (alliance.isPresent) {
                    return@configureHolonomic alliance.get() == Alliance.Red
                }
                false
            },
            subsystem // Reference to this subsystem to set requirements
        )

        PathPlannerLogging.setLogActivePathCallback { activePath: List<Pose2d> ->
            Logger.recordOutput(
                "Odometry/Trajectory", *activePath.toTypedArray<Pose2d>()
            )
        }
        PathPlannerLogging.setLogTargetPoseCallback { targetPose: Pose2d ->
            Logger.recordOutput("Odometry/TrajectorySetpoint", targetPose)
        }

        RobotPeriodicManager.startPeriodic { periodic() }
    }

    private fun periodic() {
        gyroIO.updateInputs(gyroInputs)
        Logger.processInputs("Drive/Gyro", gyroInputs)
        for (module: Module in modules) {
            module.periodic()
        }

        // Stop moving when disabled
        if (DriverStation.isDisabled()) {
            for (module: Module in modules) {
                module.stop()
            }
        }
        // Log empty setpoint states when disabled
        if (DriverStation.isDisabled()) {
            Logger.recordOutput("SwerveStates/Setpoints", *arrayOf<SwerveModuleState>())
            Logger.recordOutput("SwerveStates/SetpointsOptimized", *arrayOf<SwerveModuleState>())
        }

        // Read wheel positions and deltas from each module
        val modulePositions: Array<SwerveModulePosition> = getModulePositions()
        val moduleDeltas: Array<SwerveModulePosition> = Array(4) { moduleIndex ->
            val deltaPosition = SwerveModulePosition(
                modulePositions[moduleIndex].distanceMeters - lastModulePositions[moduleIndex].distanceMeters,
                modulePositions[moduleIndex].angle
            )

            lastModulePositions[moduleIndex] = modulePositions[moduleIndex]

            return@Array deltaPosition
        }

        // Update gyro angle
        if (gyroInputs.connected) {
            // Use the real gyro angle
            rawGyroRotation = gyroInputs.yawPosition
        } else {
            // Use the angle delta from the kinematics and module deltas
            val twist: Twist2d = kinematics.toTwist2d(*moduleDeltas)
            rawGyroRotation = rawGyroRotation.plus(Rotation2d(twist.dtheta))
        }

        // Apply odometry update
        poseEstimator.update(rawGyroRotation, modulePositions)

        LimelightHelpers.SetRobotOrientation(
            "limelight",
            poseEstimator.estimatedPosition.rotation.degrees,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0
        )
        val mt2 = LimelightHelpers.getBotPoseEstimate_wpiBlue_MegaTag2("limelight")

        poseEstimator.setVisionMeasurementStdDevs(VecBuilder.fill(.7,.7, 9999999.0));
        if (mt2 != null) {
            poseEstimator.addVisionMeasurement(
                mt2.pose,
                mt2.timestampSeconds)
        };

        Logger.recordOutput("Odometry/Robot", getPose())
    }


    /**
     * Runs the drive at the desired velocity.
     *
     * @param speeds Speeds in meters/sec
     */
    fun runRawChassisSpeeds(speeds: ChassisSpeeds) {
        // Calculate module setpoints
        val discreteSpeeds: ChassisSpeeds = ChassisSpeeds.discretize(speeds, 0.02)
        val setpointStates: Array<SwerveModuleState> = kinematics.toSwerveModuleStates(discreteSpeeds)
        SwerveDriveKinematics.desaturateWheelSpeeds(setpointStates, MAX_VELOCITY_MPS)

        // Send setpoints to modules
        val optimizedSetpointStates: Array<SwerveModuleState> = Array(4) { index ->
            // The module returns the optimized state, useful for logging
            modules[index].runSetpoint(setpointStates[index])
        }

        // Log setpoint states
        Logger.recordOutput("SwerveStates/Setpoints", *setpointStates)
        Logger.recordOutput("SwerveStates/SetpointsOptimized", *optimizedSetpointStates)
    }

    fun runFieldRelative(speeds: ChassisSpeeds) {
        runRawChassisSpeeds(ChassisSpeeds.fromFieldRelativeSpeeds(speeds, rawGyroRotation))
    }

    /** Stops the drive.  */
    fun stop() {
        runRawChassisSpeeds(ChassisSpeeds())
    }

    /**
     * Stops the drive and turns the modules to an X arrangement to resist movement. The modules will
     * return to their normal orientations the next time a nonzero velocity is requested.
     */
    fun stopWithX() {
        val headings: Array<Rotation2d?> = arrayOfNulls(4)
        for (i in 0..3) {
            headings[i] = moduleTranslations[i].angle
        }
        kinematics.resetHeadings(*headings)
        stop()
    }

    /** Returns the module states (turn angles and drive velocities) for all of the modules.  */
    private fun getModuleStates(): Array<SwerveModuleState?> {
        val states: Array<SwerveModuleState?> = arrayOfNulls(4)
        for (i in 0..3) {
            states[i] = modules[i].state
        }
        return states
    }

    /** Returns the module positions (turn angles and drive positions) for all of the modules.  */
    private fun getModulePositions() = Array(4) { index -> modules[index].position }

    /** Returns the current odometry pose.  */
    @AutoLogOutput(key = "Odometry/Robot") fun getPose(): Pose2d {
        return poseEstimator.estimatedPosition
    }

    /** Returns the current odometry rotation.  */
    fun getRotation(): Rotation2d {
        return getPose().rotation
    }

    /** Resets the current odometry pose.  */
    fun setPose(pose: Pose2d?) {
        poseEstimator.resetPosition(rawGyroRotation, getModulePositions(), pose)
    }

    /**
     * Adds a vision measurement to the pose estimator.
     *
     * @param visionPose The pose of the robot as measured by the vision camera.
     * @param timestamp The timestamp of the vision measurement in seconds.
     */
    fun addVisionMeasurement(visionPose: Pose2d?, timestamp: Double) {
        poseEstimator.addVisionMeasurement(visionPose, timestamp)
    }


    fun resetGyro() {
        gyroIO.resetGyro()
    }
}