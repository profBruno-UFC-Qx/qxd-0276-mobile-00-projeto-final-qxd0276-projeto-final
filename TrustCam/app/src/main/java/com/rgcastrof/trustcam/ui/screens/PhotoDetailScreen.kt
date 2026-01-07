package com.rgcastrof.trustcam.ui.screens

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    initialPhotoId: Int?,
    onBackClick: () -> Unit,
    onImageClick: () -> Unit,
    onExportClick: (photo: Photo) -> Unit,
    onDeleteClick: (photo: Photo) -> Unit,
) {
    when {
        photos == null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        photos.isEmpty() -> {
            LaunchedEffect(Unit) {
                onBackClick()
            }
        }

        else -> {
            Box(modifier = Modifier.safeDrawingPadding()) {
                val initialPage = remember(photos, initialPhotoId) {
                    val index = photos.indexOfFirst { it.id == initialPhotoId }
                    if (index != -1) index else 0
                }
                val pagerState = rememberPagerState(initialPage = initialPage) { photos.size }

                HorizontalPager(
                    state = pagerState,
                ) { index ->
                    AsyncImage(
                        model = photos[index].filePath,
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxSize()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onImageClick,
                            ),
                        contentScale = ContentScale.Fit
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
                        onSaveToGallery = onExportClick,
                        onDeleteClick = onDeleteClick,
                    )
                }
            }
        }
    }
}