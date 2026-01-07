package com.rgcastrof.trustcam.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.rgcastrof.trustcam.data.model.Photo

@Composable
fun CameraControls(
    onOpenGallery: () -> Unit,
    onTakePhoto: () -> Unit,
    onSwitchCamera: () -> Unit,
    lastTakenPhoto: Photo?
) {
    Row(
        modifier = Modifier .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onOpenGallery,
            modifier = Modifier.padding(42.dp)
        ) {
            if (lastTakenPhoto != null) {
                AsyncImage(
                    model = lastTakenPhoto.filePath,
                    contentDescription = "Last taken photo",
                    contentScale = ContentScale.Crop,
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Photo,
                    contentDescription = "Open gallery",
                    modifier = Modifier.size(50.dp),
                )
            }
        }

        Box(
            modifier = Modifier
                .size(65.dp)
                .clip(CircleShape)
                .background(Color.DarkGray)
                .border(2.dp, Color.White, CircleShape)
                .clickable(onClick = onTakePhoto)
        )

        IconButton(
            onClick = onSwitchCamera,
            modifier = Modifier.padding(42.dp)
        ) {
            Icon(
                imageVector = Icons.Default.FlipCameraAndroid,
                contentDescription = "Camera switch",
                modifier = Modifier.size(50.dp)
            )
        }

    }
}