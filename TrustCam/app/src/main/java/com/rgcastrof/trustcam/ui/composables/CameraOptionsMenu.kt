package com.rgcastrof.trustcam.ui.composables

import androidx.camera.core.ImageCapture
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.GridOff
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.rgcastrof.trustcam.uistate.CameraUiState

@Composable
fun CameraOptionsMenu(
    uiState: CameraUiState,
    modifier: Modifier = Modifier,
    gridStateOn: Boolean,
    onToggleFlashMode: () -> Unit,
    onToggleGridState: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Row(
        modifier = modifier
            .padding(12.dp)
            .background(shape = CircleShape, color = Color.Black.copy(alpha = 0.5f)).padding(12.dp)
    ) {
        AnimatedVisibility(
            visible = expanded
        ) {
            Row(modifier = Modifier.padding(end = 12.dp)) {
                MenuItem(
                    modifier = Modifier.padding(end = 22.dp),
                    icon = if (gridStateOn) Icons.Default.GridOn else Icons.Default.GridOff,
                    contentDescription = "Camera grid button",
                    onClick = onToggleGridState
                )
                /* TODO: Implement aspect ratio */
                Text(
                    text = "9:16",
                    modifier = Modifier.padding(end = 22.dp)
                )
                MenuItem(
                    modifier = modifier.padding(end = 10.dp),
                    icon = when(uiState.flashMode) {
                        ImageCapture.FLASH_MODE_ON -> Icons.Default.FlashOn
                        ImageCapture.FLASH_MODE_OFF -> Icons.Default.FlashOff
                        else -> Icons.Default.FlashAuto
                    },
                    contentDescription = "Camera flash button",
                    onClick = onToggleFlashMode
                )
            }
        }
        Icon(
            imageVector = if (expanded) Icons.Default.Close else Icons.Default.Apps,
            contentDescription = "Camera dashboard button",
            modifier = modifier
                .clickable { expanded = !expanded }
        )
    }
}

@Composable
private fun MenuItem(
    modifier: Modifier,
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        modifier = modifier.clickable(onClick = onClick),
    )
}