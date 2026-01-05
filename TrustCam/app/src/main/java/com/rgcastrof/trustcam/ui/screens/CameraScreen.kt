package com.rgcastrof.trustcam.ui.screens

import android.content.Context
import android.media.MediaActionSound
import android.util.Log
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.rgcastrof.trustcam.ui.composables.CameraPreview
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import com.rgcastrof.trustcam.ui.composables.CameraControls
import com.rgcastrof.trustcam.ui.composables.CameraOptionsMenu
import com.rgcastrof.trustcam.uistate.CameraUiState
import java.io.File

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun CameraScreen(
    uiState: CameraUiState,
    onPhotoSaved: (String) -> Unit,
    onSwitchCamera: () -> Unit,
    onNavigateToGallery: () -> Unit,
    onToggleFlashMode: () -> Unit,
    onToggleGridState: () -> Unit,
    context: Context
) {
    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(
                CameraController.IMAGE_CAPTURE
            )
        }
    }

    var triggerShutterEffect by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.cameraSelector) {
        controller.cameraSelector = uiState.cameraSelector
    }

    LaunchedEffect(uiState.flashMode) {
        controller.imageCaptureFlashMode = uiState.flashMode
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        val cameraPreviewModifier = Modifier.fillMaxWidth().aspectRatio(9f/16)
        Box(modifier = Modifier.fillMaxWidth()) {
            CameraPreview(
                gridState = uiState.gridStateOn,
                controller = controller,
                modifier = cameraPreviewModifier
            )
            CameraOptionsMenu(
                uiState = uiState,
                modifier = Modifier.align(Alignment.BottomEnd),
                gridStateOn = uiState.gridStateOn,
                onToggleFlashMode = onToggleFlashMode,
                onToggleGridState = onToggleGridState
            )

            ShutterEffect(
                showShutter = triggerShutterEffect,
                onAnimationEnd = { triggerShutterEffect = false },
                modifier = cameraPreviewModifier
            )
        }

        CameraControls(
            onOpenGallery = {
                onNavigateToGallery()
            },
            onTakePhoto = {
                triggerShutterEffect = true
                takePhoto(
                    context = context,
                    controller = controller,
                    onPhotoCaptured = { uriString ->
                        onPhotoSaved(uriString)
                    }
                )
            },
            onSwitchCamera = onSwitchCamera,
        )
    }
}

@Composable
fun ShutterEffect(
    showShutter: Boolean,
    onAnimationEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val alpha by animateFloatAsState(
        targetValue = if (showShutter) 1f else 0f,
        animationSpec = tween(100),
        finishedListener = {
            if (showShutter) onAnimationEnd()
        },
        label = "ShutterAnimation"
    )
    if (showShutter) {
        Box(
            modifier = modifier.background(Color.Black.copy(alpha))
        )
    }
}

private fun takePhoto(
    context: Context,
    controller: LifecycleCameraController,
    onPhotoCaptured: (String) -> Unit
) {
    MediaActionSound().play(MediaActionSound.SHUTTER_CLICK)
    val photosDir = File(context.filesDir, "my_images")
    if (!photosDir.exists()) photosDir.mkdirs()
    val photoFile = File(
        photosDir,
        "trustcam_${System.currentTimeMillis()}.jpg"
    )
    val outputFileOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
    controller.takePicture(
        outputFileOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onError(exception: ImageCaptureException) {
                Log.e("Camera", "Couldn't take photo: ", exception)
            }

            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                onPhotoCaptured(photoFile.absolutePath)
            }
        }
    )
}