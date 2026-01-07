package com.rgcastrof.trustcam.data.repository

import android.content.ContentResolver
import android.content.ContentValues
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.rgcastrof.trustcam.data.model.Photo
import com.rgcastrof.trustcam.data.dao.PhotoDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class CameraRepository(
    private val dao: PhotoDao,
    private val contentResolver: ContentResolver
) {
    suspend fun insert(photo: Photo) = dao.insertPhoto(photo)
    suspend fun delete(photo: Photo) = dao.deletePhoto(photo)
    fun getAllPhotos() = dao.getAllPhotos()

    suspend fun exportToGallery(photo: Photo): Boolean = withContext(Dispatchers.IO) {
        var insertedUri: Uri? = null

        try {
            val photosCollection =
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            val photoContentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_DCIM}/TrustCam")
                put(MediaStore.Images.Media.DISPLAY_NAME, "trustcam_${photo.timestamp}.jpg")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
                put(MediaStore.Images.Media.DATE_TAKEN, photo.timestamp)
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
            insertedUri = contentResolver.insert(photosCollection, photoContentValues)

            insertedUri?.let { uri ->
                contentResolver.openOutputStream(uri)?.use { outputStream ->
                    File(photo.filePath).inputStream().use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }

                photoContentValues.clear()
                photoContentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                contentResolver.update(uri, photoContentValues, null, null)
                Log.d("PhotoDetail", "Photo successful imported to internal storage")
                true
            } ?: false
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("PhotoDetail", "Error to import photo to internal storage")
            insertedUri?.let { uri ->
                contentResolver.delete(uri, null, null)
            }
            false
        }
    }
}