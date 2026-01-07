package com.rgcastrof.trustcam.uistate

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import com.rgcastrof.trustcam.data.model.Photo

data class CameraUiState(
    val cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
    val flashMode: Int = ImageCapture.FLASH_MODE_OFF,
    val gridStateOn: Boolean = false,
    val lastTakenPhoto: Photo? = null
)
