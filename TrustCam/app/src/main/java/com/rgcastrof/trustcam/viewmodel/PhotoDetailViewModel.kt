package com.rgcastrof.trustcam.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.rgcastrof.trustcam.data.TrustCamApplication
import com.rgcastrof.trustcam.data.model.Photo
import com.rgcastrof.trustcam.data.repository.CameraRepository
import com.rgcastrof.trustcam.uistate.PhotoDetailUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class PhotoDetailViewModel(
    private val cameraRepository: CameraRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _selectedPhotoId: Int? = savedStateHandle["photoId"]
    private val _detailOverlay = MutableStateFlow(true)
    private val _allPhotos = cameraRepository.getAllPhotos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val uiState = combine(
        _detailOverlay,
        _allPhotos
    ) { detailOverlay, photos ->
        PhotoDetailUiState(
            photos = photos,
            selectedPhotoId = _selectedPhotoId,
            detailOverlay = detailOverlay
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = PhotoDetailUiState()
    )

    fun toggleDetailOverlay() {
        _detailOverlay.update { current ->
            !current
        }
    }

    fun deletePhoto(photo: Photo) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val file = File(photo.filePath)
                if (file.exists()) {
                    val deleted = file.delete()
                    if (deleted) {
                        Log.d("PhotoDetail", "Hard file successful deleted")
                    }
                }
                cameraRepository.delete(photo)
            } catch (e: Exception) {
                Log.e("PhotoDetail", "Failed to delete photo", e)
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as TrustCamApplication)
                val repository = application.repository
                val savedStateHandle = createSavedStateHandle()
                PhotoDetailViewModel(repository, savedStateHandle)
            }
        }
    }
}