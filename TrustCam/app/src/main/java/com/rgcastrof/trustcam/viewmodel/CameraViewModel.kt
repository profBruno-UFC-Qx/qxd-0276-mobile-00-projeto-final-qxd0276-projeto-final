package com.rgcastrof.trustcam.viewmodel

import android.graphics.Bitmap
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.rgcastrof.trustcam.data.TrustCamApplication
import com.rgcastrof.trustcam.data.model.Photo
import com.rgcastrof.trustcam.data.repository.CameraRepository
import com.rgcastrof.trustcam.uistate.CameraUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CameraViewModel(
    private val cameraRepository: CameraRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CameraUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            cameraRepository.getLastPhoto().collect { photo ->
                _uiState.update { currentState ->
                    currentState.copy(lastTakenPhoto = photo)
                }
            }
        }
    }
    fun switchCamera() {
        val currentSelector = _uiState.value.cameraSelector
        val nextSelector = if (currentSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
        _uiState.update { it.copy(cameraSelector = nextSelector) }
    }

    fun toggleFlash() {
        val currentMode = _uiState.value.flashMode
        val nextMode = when(currentMode) {
            ImageCapture.FLASH_MODE_OFF -> ImageCapture.FLASH_MODE_ON
            ImageCapture.FLASH_MODE_ON -> ImageCapture.FLASH_MODE_AUTO
            else -> ImageCapture.FLASH_MODE_OFF
        }
        _uiState.update { it.copy(flashMode = nextMode) }
    }

    fun toggleGridState() {
        val currentGridState = _uiState.value.gridStateOn
        _uiState.update { it.copy(gridStateOn = !currentGridState) }
    }

    fun switchAspectRatio() {
        val currentAspectRatio = _uiState.value.aspectRatio
        val nextAspectRatio = if (currentAspectRatio == AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY)
            AspectRatioStrategy.RATIO_4_3_FALLBACK_AUTO_STRATEGY
            else
            AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY
        _uiState.update { it.copy(aspectRatio = nextAspectRatio) }
    }

    fun storePhotoInDevice(bitmap: Bitmap) {
        viewModelScope.launch {
            val savedUri = cameraRepository.saveBitmapToMediaStore(bitmap)
            if (savedUri != null) {
                val photo = Photo(
                    filePath = savedUri.toString(),
                    timestamp = System.currentTimeMillis()
                )
                cameraRepository.insert(photo)
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as TrustCamApplication)
                val repository = application.repository
                CameraViewModel(repository)
            }
        }
    }
}