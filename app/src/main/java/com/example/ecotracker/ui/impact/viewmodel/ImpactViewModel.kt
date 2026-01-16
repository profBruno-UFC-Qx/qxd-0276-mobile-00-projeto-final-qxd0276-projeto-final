package com.example.ecotracker.ui.impact.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecotracker.data.local.entity.HabitMapItem
import com.example.ecotracker.data.repository.HabitRepository
import com.example.ecotracker.data.repository.UserRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalCoroutinesApi::class)
class ImpactViewModel(
    private val habitRepository: HabitRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val dateFormatter = DateTimeFormatter.ISO_DATE
    private val today = LocalDate.now().format(dateFormatter)

    private val userIdFlow = userRepository.getLoggedUserPreference()
        .filterNotNull()
        .map { it.id }

    val uiState: StateFlow<ImpactUiState> =
        userIdFlow
            .flatMapLatest { userId ->
                combine(
                    habitRepository.countCompletedToday(userId, today),
                    habitRepository.getCompletedHabitsWithLocationByUser(userId),
                    userRepository.getUserPoints(userId)
                ) { totalCompleted, habits, points ->

                    val habitsOnMap = habits.mapNotNull { habit ->
                        val lat = habit.latitude
                        val lng = habit.longitude

                        if (lat != null && lng != null) {
                            HabitMapItem(
                                id = habit.id,
                                name = habit.name,
                                latLng = LatLng(lat, lng)
                            )
                        } else null
                    }

                    ImpactUiState(
                        totalHabitsCompleted = totalCompleted,
                        points = points,
                        habitsLocations = habitsOnMap
                    )
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = ImpactUiState()
            )
}
