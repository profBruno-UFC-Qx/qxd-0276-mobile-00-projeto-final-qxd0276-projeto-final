package com.rgcastrof.trustcam

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rgcastrof.trustcam.ui.screens.CameraScreen
import com.rgcastrof.trustcam.ui.screens.PhotoDetailScreen
import com.rgcastrof.trustcam.viewmodel.CameraViewModel
import com.rgcastrof.trustcam.viewmodel.PhotoDetailViewModel

sealed class Screen(val route: String) {
    object CameraScreen : Screen("camera_screen")
    object PhotoDetailScreen : Screen(route = "photo_detail_screen/{photoId}") {
        fun createRoute(photoId: Int) = "photo_detail_screen/$photoId"
    }
}

@Composable
fun TrustCamNavigation(context: Context) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.CameraScreen.route) {
        composable(route = Screen.CameraScreen.route) {
            val viewModel: CameraViewModel = viewModel(factory = CameraViewModel.Factory)
            val uiState by viewModel.uiState.collectAsState()
            CameraScreen(
                uiState = uiState,
                onSwitchCamera = viewModel::switchCamera,
                storePhotoInDevice = viewModel::storePhotoInDevice,
                onNavigateToGallery = {
                    uiState.lastTakenPhoto?.let { photo ->
                        navController.navigate(Screen.PhotoDetailScreen.createRoute(photo.id))
                    }
                },
                context = context,
                onToggleFlashMode = viewModel::toggleFlash,
                onToggleGridState = viewModel::toggleGridState,
                onToggleAspectRatio = viewModel::switchAspectRatio,
                onToggleLocation = viewModel::toggleLocation
            )
        }

        composable(
            route = Screen.PhotoDetailScreen.route,
        ) {
            val viewModel: PhotoDetailViewModel = viewModel(factory = PhotoDetailViewModel.Factory)
            val uiState by viewModel.uiState.collectAsState()

            PhotoDetailScreen(
                context = context,
                showOverlay = uiState.detailOverlay,
                photos = uiState.photos,
                onBackClick = { navController.popBackStack() },
                onDeleteClick = viewModel::deletePhoto,
                onImageClick = viewModel::toggleDetailOverlay,
                onChangedPhotoDescription = viewModel::changePhotoDescription
            )
        }
    }
}