package com.rgcastrof.trustcam.ui.screens

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.rgcastrof.trustcam.data.model.Photo
import com.rgcastrof.trustcam.ui.composables.PhotoDetailOverlay
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun PhotoDetailScreen(
    context: Context,
    photos: List<Photo>?,
    showOverlay: Boolean,
    onBackClick: () -> Unit,
    onImageClick: () -> Unit,
    onDeleteClick: (photo: Photo) -> Unit,
) {
    LaunchedEffect(photos) {
        if (photos != null && photos.isEmpty()) {
            onBackClick()
        }
    }

    if (!photos.isNullOrEmpty()) {
        val pagerState = rememberPagerState { photos.size }

        Box(modifier = Modifier.safeDrawingPadding()) {
            HorizontalPager(
                state = pagerState,
                key = { photos[it].id }
            ) { index ->
                BoxWithPhoto(
                    photoPath = photos[index].filePath,
                    contentDescription = "Taken photo",
                    modifier = Modifier.fillMaxWidth()
                        .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onImageClick,
                    )
                )
            }
            val currentPage = photos[pagerState.currentPage.coerceAtMost(photos.lastIndex)]
            val dateFormat = DateTimeFormatter
                .ofPattern("MMM dd, yyyy\nHH:mm:ss")
                .withZone(ZoneId.systemDefault())
                .format(Instant.ofEpochMilli(currentPage.timestamp))

            if (showOverlay) {
                PhotoDetailOverlay(
                    context = context,
                    currentPage = currentPage,
                    dateFormat = dateFormat,
                    onBackClick = onBackClick,
                    onDeleteClick = onDeleteClick,
                )
            }
        }
    }
}

@Composable
fun BoxWithPhoto(
    photoPath: String,
    contentDescription: String,
    modifier: Modifier
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth()
    ) {
        val state = rememberTransformableState { zoomChange, panChange, _ ->
            scale = (scale * zoomChange).coerceIn(1f, 5f)
            val extraWidth = (scale - 1) * constraints.maxWidth
            val extraHeight = (scale - 1) * constraints.maxHeight

            val maxX = extraWidth / 2
            val maxY = extraHeight / 2

            offset = Offset(
                x = (offset.x + panChange.x * scale).coerceIn(-maxX, maxX),
                y = (offset.y + panChange.y * scale).coerceIn(-maxY, maxY)
            )
        }

        AsyncImage(
            model = photoPath,
            contentDescription = contentDescription,
            modifier = modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    translationX = offset.x
                    translationY = offset.y
                }
                .transformable(state),
            contentScale = ContentScale.Fit
        )
    }
}