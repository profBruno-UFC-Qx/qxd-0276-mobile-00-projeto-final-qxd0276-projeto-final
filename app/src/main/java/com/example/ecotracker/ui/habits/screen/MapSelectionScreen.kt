import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ecotracker.ui.habits.viewmodel.MapSelectionViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapSelectionScreen(
    viewModel: MapSelectionViewModel = viewModel(),
    onPlaceSelected: (SelectedPlace) -> Unit,
    onCancel: () -> Unit
) {
    val deviceLocation by viewModel.deviceLocation.collectAsState()
    val error by viewModel.error.collectAsState()

    val cameraPositionState = rememberCameraPositionState()

    LaunchedEffect(deviceLocation) {
        deviceLocation?.let {
            cameraPositionState.position =
                CameraPosition.fromLatLngZoom(it, 15f)
        }
    }

    error?.let {
        Snackbar { Text(it) }
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        onMapClick = { latLng ->
            onPlaceSelected(
                SelectedPlace(null, "Local selecionado no mapa", latLng)
            )
        }
    ) {
        deviceLocation?.let {
            Marker(state = MarkerState(it))
        }
    }
}
