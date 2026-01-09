package com.rgcastrof.trustcam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.rgcastrof.trustcam.ui.theme.TrustCamTheme
import com.rgcastrof.trustcam.utils.PermissionUtils

class MainActivity : ComponentActivity() {
    private val cameraPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            setAppScreen()
        } else {
            Toast.makeText(
                this@MainActivity,
                "This app needs permission to take photos",
                Toast.LENGTH_LONG
            ).show()
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (PermissionUtils.hasCameraPermission(this)) {
            setAppScreen()
        } else {
            cameraPermissionRequest.launch(Manifest.permission.CAMERA)
        }
    }


    private fun setAppScreen() {
        enableEdgeToEdge()
        setContent {
            TrustCamTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TrustCamNavigation(this@MainActivity)
                }
            }
        }
    }
}