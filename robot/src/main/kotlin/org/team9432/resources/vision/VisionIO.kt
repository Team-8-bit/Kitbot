package org.team9432.resources.vision

import org.photonvision.targeting.PhotonPipelineResult
import org.team9432.annotation.Logged

interface VisionIO {
    @Logged
    open class VisionIOInputs {
        var results: PhotonPipelineResult = PhotonPipelineResult()
        var isConnected: Boolean = false
    }

    /** Updates the set of loggable inputs.  */
    fun updateInputs(inputs: VisionIOInputs) {}
}