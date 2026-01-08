package com.example.ecotracker.ui.habits.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.ecotracker.data.local.entity.Habit
import com.example.ecotracker.data.repository.HabitRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi

enum class HabitFilter {
    ALL,
    COMPLETED,
    PENDING
}
@OptIn(ExperimentalCoroutinesApi::class)
class HabitViewModel(
    private val repository: HabitRepository
) : ViewModel() {
    // Estado para as operações do CRUD
    private val _uiState = MutableStateFlow<HabitOperationUiState>(HabitOperationUiState.Idle)
    val uiState: StateFlow<HabitOperationUiState> = _uiState.asStateFlow()

    // Estado para controlar o filtro aplicado
    private val _habitFilter = MutableStateFlow(HabitFilter.ALL)
    val habitFilter: StateFlow<HabitFilter> = _habitFilter.asStateFlow()

    // Armazena a contagem de hábitos
    val totalHabitsCount: StateFlow<Int> = repository.countHabits()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val completedHabitsCount: StateFlow<Int> = repository.countCompletedHabits()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _userIdForFilter = MutableStateFlow(1L)

    // Lista de Hábitos
    val habits: Flow<PagingData<Habit>> = _habitFilter
        .flatMapLatest { filter ->
        when (filter) {
            HabitFilter.ALL -> repository.getHabitsByUser(_userIdForFilter.value)
            HabitFilter.COMPLETED -> repository.getCompletedHabits()
            HabitFilter.PENDING -> repository.getPendingHabits()
        }.cachedIn(viewModelScope)
    }

    fun setHabitFilter(filter: HabitFilter){
        _habitFilter.value = filter
    }
    // Funções CRUD
    fun addHabit(
        name: String,
        userId : Long,
        description: String,
        latitude: Double?,
        longitude: Double?,
        locationName: String?
    ){
        viewModelScope.launch {
            _uiState.value = HabitOperationUiState.Loading

            //  Cria o objeto que será adicionado ao banco
            val newHabit = Habit (
                name = name,
                userId = userId,
                description = description,
                latitude = latitude,
                longitude = longitude,
                locationName = locationName,
                isCompleted = false,
            )
            // Chama o repository e insere o novo hábito
            val result = repository.insertHabit(newHabit)

            // Atualiza o estado conforme o result
            _uiState.value = if (result.isSuccess){
                HabitOperationUiState.HabitAdded
            } else {
                val erroMessage = result.exceptionOrNull()?.message ?: "Erro desconhecido"
                HabitOperationUiState.Error(erroMessage)
            }

        }
    }
    fun updateHabit(habit: Habit) {
        viewModelScope.launch {
            _uiState.value = HabitOperationUiState.Loading
            val result = repository.updateHabit(habit)
            _uiState.value = if (result.isSuccess){
                HabitOperationUiState.HabitUpdated
            } else{
                val erroMessage = result.exceptionOrNull()?.message ?: "erro desconhecido"
                HabitOperationUiState.Error(erroMessage)
            }

        }
    }

    fun deleteHabitById(habitId: Long) {
        viewModelScope.launch {
            _uiState.value = HabitOperationUiState.Loading
            val result = repository.deleteHabitById(habitId)
            _uiState.value = if (result.isSuccess){
                HabitOperationUiState.HabitDeleted
            } else{
                val erroMessage = result.exceptionOrNull()?.message ?: "erro desconhecido"
                HabitOperationUiState.Error(erroMessage)
            }
        }
    }


    fun resetState() {
        _uiState.value = HabitOperationUiState.Idle
    }

}