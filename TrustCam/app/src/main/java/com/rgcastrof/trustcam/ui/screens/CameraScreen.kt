package com.rgcastrof.trustcam.ui.screens

import android.content.Context
import android.media.MediaActionSound
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
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

        }

        CameraControls(
            onOpenGallery = {
                onNavigateToGallery()
            },
            onTakePhoto = {
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

private fun takePhoto(
    context: Context,
    controller: LifecycleCameraController,
    onPhotoCaptured: (String) -> Unit
) {
    MediaActionSound().play(MediaActionSound.SHUTTER_CLICK)

    val photosDir = File(context.filesDir, "photos").apply {
        if (!exists()) mkdirs()
    }

    val photoFile = File(photosDir, "trustcam_${System.currentTimeMillis()}.jpg")

    val outputFileOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
    controller.takePicture(
        outputFileOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onError(exception: ImageCaptureException) {
                Toast.makeText(context, "Failed to take photo", Toast.LENGTH_SHORT)
                    .show()
                Log.e("Camera", "Couldn't take photo: ", exception)
            }

            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                onPhotoCaptured(photoFile.absolutePath)
                Log.d("Camera", "Photo saved at: ${photoFile.absolutePath}")
            }
        }
    )
}