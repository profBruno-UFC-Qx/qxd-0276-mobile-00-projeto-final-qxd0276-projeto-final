package com.rgcastrof.trustcam.ui.composables

import androidx.camera.core.AspectRatio
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CameraOverlay(
    gridState: Boolean,
    aspectRatio: Int,
    maskColor: Color = Color.Black.copy(alpha = 0.7f)
) {
    val targetRatioValue = if (aspectRatio == AspectRatio.RATIO_16_9) 16f/9f else 4f/3f

    val animatedRatio by animateFloatAsState(
        targetValue = targetRatioValue,
        animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        ),
        label = "AspectRatioAnimation"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val screenWidth = size.width
        val screenHeight = size.height
        val bottomBarHeight = 240f

        val targetHeight = screenWidth * animatedRatio
        val topBarHeight = (screenHeight - targetHeight - bottomBarHeight).coerceAtLeast(0f)

        if (topBarHeight > 0) {
            drawRect(
                color = maskColor,
                topLeft = Offset.Zero,
                size = Size(screenWidth, topBarHeight)
            )
        }
        drawRect(
            color = maskColor,
            topLeft = Offset(0f, screenHeight - bottomBarHeight),
            size = Size(screenWidth, bottomBarHeight)
        )

        if (gridState) {
            val gridHeight = size.height - topBarHeight - bottomBarHeight
            val strokeWidth = 1.dp.toPx()
            val gridColor = Color.White.copy(alpha = 0.7f)

            drawLine(
                color = gridColor,
                start = Offset(x = screenWidth / 3, y = topBarHeight),
                end = Offset(x = screenWidth / 3, y = topBarHeight + gridHeight),
                strokeWidth = strokeWidth
            )

            drawLine(
                color = gridColor,
                start = Offset(x = (screenWidth / 3) * 2, y = topBarHeight),
                end = Offset(x = (screenWidth / 3) * 2, y = topBarHeight + gridHeight),
                strokeWidth = strokeWidth
            )

            drawLine(
                color = gridColor,
                start = Offset(x = 0f, y = topBarHeight + (gridHeight / 3)),
                end = Offset(x = screenWidth, y = topBarHeight + (gridHeight / 3)),
                strokeWidth = strokeWidth
            )

            drawLine(
                color = gridColor,
                start = Offset(x = 0f, y = topBarHeight + (gridHeight / 3) * 2),
                end = Offset(x = screenWidth, y = topBarHeight + (gridHeight / 3) * 2),
                strokeWidth = strokeWidth
            )
        }
    }
}