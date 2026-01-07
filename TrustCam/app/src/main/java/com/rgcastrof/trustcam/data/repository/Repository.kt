package com.rgcastrof.trustcam.data.repository

import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.rgcastrof.trustcam.data.model.Photo
import com.rgcastrof.trustcam.data.dao.PhotoDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class CameraRepository(
    private val dao: PhotoDao,
    private val contentResolver: ContentResolver
) {
    suspend fun insert(photo: Photo) = dao.insertPhoto(photo)
    suspend fun delete(photo: Photo) = dao.deletePhoto(photo)
    fun getAllPhotos() = dao.getAllPhotos()

    suspend fun saveBitmapToMediaStore(capturedPhotoBitmap: Bitmap): Uri? = withContext(Dispatchers.IO) {
        val timestamp = System.currentTimeMillis()
        val filename = "trustcam_$timestamp.jpg"
        var imageUri: Uri? = null
        val contentValues: ContentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_DCIM}/TrustCam")
            put(MediaStore.Images.Media.DATE_TAKEN, timestamp)
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        try {
            val contentUri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            imageUri = contentResolver.insert(contentUri, contentValues)

            imageUri?.let { uri ->
                contentResolver.openOutputStream(uri)?.use { outputStream ->
                    if (!capturedPhotoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)) {
                        throw IOException("Failed to compress bitmap")
                    }
                }
                contentValues.clear()
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                contentResolver.update(uri, contentValues, null, null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            imageUri?.let { contentResolver.delete(it, null, null) }
            return@withContext null
        }
        return@withContext imageUri
    }
}