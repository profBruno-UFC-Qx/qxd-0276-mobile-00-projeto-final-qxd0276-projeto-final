package com.rgcastrof.trustcam.ui.composables

import android.content.ClipData
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PhotoCameraBack
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.toClipEntry
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.rgcastrof.trustcam.data.model.Photo
import kotlinx.coroutines.launch

@Composable
fun InformationBottomSheet(
    photo: Photo,
    onChangePhotoDescription: (newDescription: String, photo: Photo) -> Unit
) {
    val context = LocalContext.current
    val locationText = remember(photo.latitude, photo.longitude)
        { "${photo.latitude}, ${photo.longitude}" }
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.wrapContentHeight()
    ) {
        DescriptionTextField(
            photo,
            onChangePhotoDescription = onChangePhotoDescription
        )
        InformationRow(
            modifier = Modifier.padding(start = 10.dp, top = 4.dp),
            icon = Icons.Default.PhoneAndroid,
            contentDescription = "Phone model",
            text = "${Build.MANUFACTURER} ${Build.MODEL}"
        )

        InformationRow(
            modifier = Modifier.padding(start = 10.dp, top = 4.dp),
            icon = Icons.Default.PhotoCameraBack,
            contentDescription = "Photo metadata",
            text = "trustcam_${photo.timestamp}.jpg"
        )

        if (photo.wasLocationDetected) {
            InformationRow(
                modifier = Modifier
                    .padding(start = 10.dp, top = 4.dp)
                    .clickable {
                        scope.launch {
                            try {
                                val clipData = ClipData.newPlainText("Photo location", locationText)
                                clipboard.setClipEntry(clipData.toClipEntry())
                            } catch (e : Exception) {
                                e.printStackTrace()
                                Toast.makeText(context, "Error to copy content to clipboard", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                icon = Icons.Default.Map,
                contentDescription = "Photo location",
                text = "${photo.latitude}, ${photo.longitude}"
            )
        }
    }
}

@Composable
fun DescriptionTextField(
    photo: Photo,
    onChangePhotoDescription: (newDescription: String, photo: Photo) -> Unit
) {
    var descriptionText by remember { mutableStateOf(photo.description) }
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        value = descriptionText,
        onValueChange = { descriptionText = it },
        label = { Text("Add description") },
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        colors = TextFieldDefaults.colors(
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent
        ),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = {
                onChangePhotoDescription(descriptionText, photo)
                focusManager.clearFocus()
            }
        )
    )
}

@Composable
fun InformationRow(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    contentDescription: String,
    text: String,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(bottom = 15.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(30.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            modifier = Modifier
        )
    }
}