package com.rgcastrof.trustcam.ui.composables

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.rgcastrof.trustcam.data.model.Photo
import java.io.File

@Composable
fun PhotoDetailOverlay(
    context: Context,
    currentPage: Photo,
    dateFormat: String,
    onBackClick: () -> Unit,
    onDeleteClick: (Photo) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(modifier = Modifier.align(Alignment.TopStart)) {
            IconButton(
                onClick = onBackClick,
            ) {
                Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = null)
            }
            Text(
                text = dateFormat,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 5.dp),
                fontWeight = FontWeight.Bold
            )
        }
        IconButton(
            onClick = { TODO() },
            modifier = Modifier.align(Alignment.TopEnd),
        ) {
            Icon(
                Icons.Default.MoreVert,
                contentDescription = null
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth()
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ButtonWithIconAndLabel(
                icon = Icons.Default.Share,
                contentDescription = "Share photo",
                label = "Share",
                onClick = {
                    sharePhoto(
                        context = context,
                        filePath = currentPage.filePath
                    )
                }
            )

            ButtonWithIconAndLabel(
                icon = Icons.Default.SaveAlt,
                contentDescription = "Save the photo in the internal storage",
                label = "Save to gallery",
                onClick = {},
            )

            ButtonWithIconAndLabel(
                icon = Icons.Default.DeleteOutline,
                contentDescription = "Delete the photo",
                label = "Delete",
                onClick = {
                    onDeleteClick(currentPage)
                }
            )
        }
    }
}

@Composable
fun ButtonWithIconAndLabel(
    icon: ImageVector,
    contentDescription: String,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(10.dp)
    ) {
        Icon(
            modifier = Modifier.size(30.dp),
            imageVector = icon,
            contentDescription = contentDescription
        )
        Text(
            text = label,
            fontSize = 12.sp,
        )
    }
}

private fun sharePhoto(context: Context, filePath: String) {
    try {
        val file = File(filePath)
        val contentUri: Uri = FileProvider.getUriForFile(
            context,
            "com.rgcastrof.trustcam.fileprovider",
            file
        )

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