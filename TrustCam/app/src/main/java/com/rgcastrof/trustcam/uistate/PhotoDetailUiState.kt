package com.rgcastrof.trustcam.uistate

import com.rgcastrof.trustcam.data.model.Photo

data class PhotoDetailUiState(
    val photos: List<Photo>? = null,
    val detailOverlay: Boolean = true
)