package com.rgcastrof.trustcam.ui.composables

import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CameraGridOverlay(
    modifier: Modifier = Modifier,
    aspectRatio: AspectRatioStrategy
) {
    val currentRatio = if (aspectRatio == AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY)
        (9f/16f)
    else
        (3f/4f)

    Canvas(modifier = modifier.fillMaxSize().aspectRatio(currentRatio)) {
        val width = size.width
        val height = size.height
        val strokeWidth = 1.dp.toPx()
        val color = Color.White.copy(alpha = 0.7f)

        drawLine(
            color = color,
            start = Offset(x = width / 3, y = 0f),
            end = Offset(x = width / 3, y = height),
            strokeWidth = strokeWidth
        )

        drawLine(
            color = color,
            start = Offset(x = (width / 3) * 2, y = 0f),
            end = Offset(x = (width / 3) * 2, y = height),
            strokeWidth = strokeWidth
        )

        drawLine(
            color = color,
            start = Offset(x = 0f, y = height / 3),
            end = Offset(x = width, y = height / 3),
            strokeWidth = strokeWidth
        )

        drawLine(
            color = color,
            start = Offset(x = 0f, y = (height / 3) * 2),
            end = Offset(x = width, y = (height / 3) * 2),
            strokeWidth = strokeWidth
        )
    }
}