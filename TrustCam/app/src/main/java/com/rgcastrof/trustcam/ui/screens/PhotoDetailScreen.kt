package com.rgcastrof.trustcam.ui.screens

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.rgcastrof.trustcam.data.model.Photo
import com.rgcastrof.trustcam.ui.composables.PhotoDetailOverlay
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
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
                ZoomablePhoto(
                    photoPath = photos[index].filePath,
                    contentDescription = "Taken photo",
                    modifier = Modifier.fillMaxWidth(),
                    onImageClick = onImageClick
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
fun ZoomablePhoto(
    photoPath: String,
    contentDescription: String,
    modifier: Modifier,
    onImageClick: () -> Unit
) {

    val zoomState = rememberZoomState()
    AsyncImage(
        model = photoPath,
        contentDescription = contentDescription,
        modifier = modifier.fillMaxSize()
            .zoomable(zoomState)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onImageClick,
            ),
        contentScale = ContentScale.Fit,
        onSuccess = { state ->
            zoomState.setContentSize(state.painter.intrinsicSize)
        }
    )
}