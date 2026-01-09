package com.rgcastrof.trustcam.utils

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri

object PhotoUtils {
    fun sharePhoto(context: Context, filePath: String) {
        try {
            val contentUri = filePath.toUri()

            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, contentUri)
                type = "image/jpg"
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val shareIntent = Intent.createChooser(sendIntent, "Share photo")
            context.startActivity(shareIntent)
        } catch (e: Exception) {
            Log.e("PhotoDetail", "Error sharing photo: ${e.message}")
            Toast.makeText(context, "Couldn't open sharing options.", Toast.LENGTH_SHORT).show()
        }
    }
}