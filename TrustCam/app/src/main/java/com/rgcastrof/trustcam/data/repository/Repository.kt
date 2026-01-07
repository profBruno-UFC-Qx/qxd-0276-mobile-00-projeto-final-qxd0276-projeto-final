package com.rgcastrof.trustcam.data.repository

import com.rgcastrof.trustcam.data.model.Photo
import com.rgcastrof.trustcam.data.dao.PhotoDao

class CameraRepository(
    private val dao: PhotoDao
) {
    suspend fun insert(photo: Photo) = dao.insertPhoto(photo)
    suspend fun delete(photo: Photo) = dao.deletePhoto(photo)
    fun getAllPhotos() = dao.getAllPhotos()

}