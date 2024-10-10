package org.team9432.resources.vision

import org.photonvision.PhotonCamera


class VisionIOReal: VisionIO {
    private val camera = PhotonCamera(VisionConstants.CAMERA_NAME)

    override fun updateInputs(inputs: VisionIO.VisionIOInputs) {
        inputs.results = camera.latestResult
        inputs.isConnected = camera.isConnected
    }
}