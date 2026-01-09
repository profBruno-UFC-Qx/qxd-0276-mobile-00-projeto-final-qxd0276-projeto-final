package com.rgcastrof.trustcam.ui.composables

import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.rgcastrof.trustcam.data.model.Photo

@Composable
fun InformationBottomSheet(
    photo: Photo,
    onChangePhotoDescription: (newDescription: String, photo: Photo) -> Unit
) {
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

        if (photo.wasLocationEnabled) {
            InformationRow(
                modifier = Modifier.padding(start = 10.dp, top = 4.dp),
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
    modifier: Modifier,
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
        Text(
            text = text,
            modifier = modifier
        )
    }
}