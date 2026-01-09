package com.rgcastrof.trustcam.ui.composables

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rgcastrof.trustcam.data.model.Photo
import com.rgcastrof.trustcam.utils.PhotoUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoDetailOverlay(
    context: Context,
    currentPage: Photo,
    dateFormat: String,
    onBackClick: () -> Unit,
    onDeleteClick: (Photo) -> Unit,
    onChangedPhotoDescription: (newDescription: String, photo: Photo) -> Unit
) {
    var openBottomSheet by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(modifier = Modifier.align(Alignment.TopStart)) {
            IconButton(
                onClick = onBackClick,
            ) {
                Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = null)
            }
            Text(
                text = dateFormat,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 5.dp),
                fontWeight = FontWeight.Bold
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth()
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ButtonWithIconAndLabel(
                icon = Icons.Default.Share,
                contentDescription = "Share photo",
                label = "Share",
                onClick = {
                    PhotoUtils.sharePhoto(
                        context = context,
                        filePath = currentPage.filePath
                    )
                }
            )

            ButtonWithIconAndLabel(
                icon = Icons.Default.Info,
                contentDescription = "Photo information",
                label = "Information",
                onClick = { openBottomSheet = true }
            )

            ButtonWithIconAndLabel(
                icon = Icons.Default.DeleteOutline,
                contentDescription = "Delete the photo",
                label = "Delete",
                onClick = {
                    onDeleteClick(currentPage)
                }
            )
        }
    }

    if (openBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { openBottomSheet = false },
            sheetState = bottomSheetState
        ) {
            InformationBottomSheet(
                currentPage,
                onChangePhotoDescription = onChangedPhotoDescription
            )
        }
    }
}

@Composable
fun ButtonWithIconAndLabel(
    icon: ImageVector,
    contentDescription: String,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(10.dp)
    ) {
        Icon(
            modifier = Modifier.size(30.dp),
            imageVector = icon,
            contentDescription = contentDescription
        )
        Text(
            text = label,
            fontSize = 12.sp,
        )
    }
}