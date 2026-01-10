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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.rgcastrof.trustcam.data.model.Photo
import com.rgcastrof.trustcam.ui.composables.PhotoDetailOverlay
import com.rgcastrof.trustcam.utils.FormatterUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

@Composable
fun PhotoDetailScreen(
    context: Context,
    photos: List<Photo>?,
    showOverlay: Boolean,
    onBackClick: () -> Unit,
    onImageClick: () -> Unit,
    onDeleteClick: (photo: Photo) -> Unit,
    onChangedPhotoDescription: (newDescription: String, photo: Photo) -> Unit
) {
    val imageLoader = remember {
        ImageLoader.Builder(context)
        .interceptorCoroutineContext(Dispatchers.IO)
        .fetcherCoroutineContext(Dispatchers.IO)
        .decoderCoroutineContext(Dispatchers.Default)
        .build()
    }

    var showContent by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(230)
        showContent = true
    }

    LaunchedEffect(photos) {
        if (photos != null && photos.isEmpty()) {
            onBackClick()
        }
    }

    if (!photos.isNullOrEmpty()) {
        Box(modifier = Modifier.safeDrawingPadding()) {
            if (showContent) {
                val pagerState = rememberPagerState { photos.size }
                HorizontalPager(
                    state = pagerState,
                    key = { photos[it].id },
                    beyondViewportPageCount = 0
                ) { index ->
                    ZoomablePhoto(
                        photoPath = photos[index].filePath,
                        contentDescription = "Taken photo",
                        modifier = Modifier.fillMaxWidth(),
                        onImageClick = onImageClick,
                        imageLoader = imageLoader
                    )
                }
                val currentPage = photos[pagerState.currentPage.coerceAtMost(photos.lastIndex)]
                val dateFormat = remember(currentPage.timestamp) {
                    FormatterUtils.formatTimestamp(currentPage.timestamp)
                }

                if (showOverlay) {
                    PhotoDetailOverlay(
                        context = context,
                        currentPage = currentPage,
                        dateFormat = dateFormat,
                        onBackClick = onBackClick,
                        onDeleteClick = onDeleteClick,
                        onChangedPhotoDescription = onChangedPhotoDescription
                    )
                }
            }
        }
    }
}

@Composable
fun ZoomablePhoto(
    photoPath: String,
    contentDescription: String,
    modifier: Modifier,
    onImageClick: () -> Unit,
    imageLoader: ImageLoader
) {
    val zoomState = rememberZoomState()
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(photoPath)
            .crossfade(true)
            .build(),
        imageLoader = imageLoader,
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