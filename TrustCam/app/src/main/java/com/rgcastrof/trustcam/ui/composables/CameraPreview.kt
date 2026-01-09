package com.rgcastrof.trustcam.ui.composables

import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraPreview(
    controller: LifecycleCameraController,
    modifier: Modifier = Modifier,
    gridState: Boolean,
    aspectRatio: AspectRatioStrategy
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    AndroidView(
        factory = {
            val previewView = PreviewView(it).apply {
                layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                implementationMode = PreviewView.ImplementationMode.PERFORMANCE
                scaleType = PreviewView.ScaleType.FIT_CENTER
                this.controller = controller
                controller.bindToLifecycle(lifecycleOwner)
            }

            val listener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    val currentZoomRatio = controller.zoomState.value?.zoomRatio ?: 1f
                    val delta = detector.scaleFactor
                    controller.setZoomRatio(currentZoomRatio * delta)
                    return true
                }
            }
            val scaleGestureDetector = ScaleGestureDetector(it, listener)

            previewView.setOnTouchListener { v, event ->
                scaleGestureDetector.onTouchEvent(event)
                if (event.action == MotionEvent.ACTION_UP)
                    v.performClick()
                return@setOnTouchListener true
            }
            previewView
        },
        modifier = modifier
    )
    if (gridState) { CameraGridOverlay(aspectRatio = aspectRatio) }
}