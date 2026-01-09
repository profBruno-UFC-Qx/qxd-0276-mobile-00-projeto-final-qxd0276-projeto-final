package com.rgcastrof.trustcam.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
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

class PhotoDetailViewModel(
    private val cameraRepository: CameraRepository,
) : ViewModel() {
    private val _detailOverlay = MutableStateFlow(true)
    private val _allPhotos = cameraRepository.getAllPhotos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val uiState = combine(
        _detailOverlay,
        _allPhotos,
    ) { detailOverlay, photos ->
        PhotoDetailUiState(
            photos = photos,
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

    fun changePhotoDescription(newDescription: String, photo: Photo) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val updatedPhoto = cameraRepository.changePhotoDescription(newDescription, photo)
                Log.d("PhotoDetail", "Photo description updated successful: ${updatedPhoto.description}")
            } catch (e: Exception) {
                Log.e("PhotoDetail", "Error to update photo description.", e)
            }
        }
    }

    fun deletePhoto(photo: Photo) {
        viewModelScope.launch {
            try {
                cameraRepository.deleteFromRoomAndDevice(photo)
            } catch (e: Exception) {
                Log.e("PhotoDetail", "Error to delete photo: ${e.message}")
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as TrustCamApplication)
                val repository = application.repository
                PhotoDetailViewModel(repository)
            }
        }
    }
}