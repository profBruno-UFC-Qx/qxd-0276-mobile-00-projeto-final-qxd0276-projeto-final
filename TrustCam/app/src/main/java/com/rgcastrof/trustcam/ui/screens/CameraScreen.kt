package com.rgcastrof.trustcam.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.MediaActionSound
import android.util.Log
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.rgcastrof.trustcam.ui.composables.CameraPreview
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.core.content.ContextCompat
import com.rgcastrof.trustcam.ui.composables.CameraControls
import com.rgcastrof.trustcam.ui.composables.CameraOptionsMenu
import com.rgcastrof.trustcam.ui.composables.CameraOverlay
import com.rgcastrof.trustcam.uistate.CameraUiState

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun CameraScreen(
    uiState: CameraUiState,
    onSwitchCamera: () -> Unit,
    onNavigateToGallery: () -> Unit,
    onToggleFlashMode: () -> Unit,
    onToggleGridState: () -> Unit,
    onToggleAspectRatio: () -> Unit,
    storePhotoInDevice: (Bitmap) -> Unit,
    context: Context
) {
    val mediaActionSound = remember { MediaActionSound() }

    DisposableEffect(Unit) {
        mediaActionSound.load(MediaActionSound.SHUTTER_CLICK)
        onDispose { mediaActionSound.release() }
    }

    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(
                CameraController.IMAGE_CAPTURE
            )
        }
    }

    LaunchedEffect(uiState.cameraSelector) {
        controller.cameraSelector = uiState.cameraSelector
    }

    LaunchedEffect(uiState.flashMode) {
        controller.imageCaptureFlashMode = uiState.flashMode
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        CameraPreview(
            controller = controller,
            modifier = Modifier.fillMaxSize()
        )

        CameraOverlay(
            aspectRatio = uiState.aspectRatio,
            gridState = uiState.gridStateOn
        )

        CameraOptionsMenu(
            uiState = uiState,
            modifier = Modifier.align(Alignment.BottomEnd),
            gridStateOn = uiState.gridStateOn,
            aspectRatio = uiState.aspectRatio,
            onToggleFlashMode = onToggleFlashMode,
            onToggleGridState = onToggleGridState,
            onToggleAspectRatio = onToggleAspectRatio
        )

        CameraControls(
            modifier = Modifier.align(Alignment.BottomCenter),
            onOpenGallery = {
                onNavigateToGallery()
            },
            onTakePhoto = {
                mediaActionSound.play(MediaActionSound.SHUTTER_CLICK)
                takePhoto(
                    context = context,
                    controller = controller,
                    onPhotoCaptured = storePhotoInDevice
                )
            },
            onSwitchCamera = onSwitchCamera,
            lastTakenPhoto = uiState.lastTakenPhoto
        )
    }
}

private fun takePhoto(
    context: Context,
    controller: LifecycleCameraController,
    onPhotoCaptured: (Bitmap) -> Unit
) {
    controller.takePicture(
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)

                val matrix = Matrix().apply {
                    postRotate(image.imageInfo.rotationDegrees.toFloat())
                    postScale(-1f, 1f)
                }

                val rotatedBitmap = Bitmap.createBitmap(
                    image.toBitmap(),
                    0,
                    0,
                    image.width,
                    image.height,
                    matrix,
                    true
                )

                onPhotoCaptured(rotatedBitmap)
                image.close()
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                Log.e("Camera", "Couldn't take photo: $exception")
            }
        }
    )
}
