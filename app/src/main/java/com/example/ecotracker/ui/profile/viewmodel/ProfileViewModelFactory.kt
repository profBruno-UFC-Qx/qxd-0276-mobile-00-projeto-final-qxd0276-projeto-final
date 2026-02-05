import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.ecotracker.EcoTrackerApplication
import com.example.ecotracker.ui.profile.viewmodel.ProfileViewModel

object ProfileViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras
    ): T {
        val application =
            extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                    as EcoTrackerApplication

        return ProfileViewModel(
            userRepository = application.container.userRepository
        ) as T
    }
}
