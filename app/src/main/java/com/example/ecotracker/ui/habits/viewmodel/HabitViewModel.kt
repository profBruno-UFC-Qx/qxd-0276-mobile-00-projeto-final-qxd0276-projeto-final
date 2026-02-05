package com.example.ecotracker.ui.habits.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.ecotracker.data.datastore.UserPreferences
import com.example.ecotracker.data.datastore.UserSession
import com.example.ecotracker.data.local.entity.Habit
import com.example.ecotracker.data.repository.HabitRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter

enum class HabitFilter {
    ALL,
    COMPLETED,
    PENDING
}
@OptIn(ExperimentalCoroutinesApi::class)
class HabitViewModel(
    private val repository: HabitRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {
    // Estado para as operações do CRUD
    private val _uiState = MutableStateFlow<HabitOperationUiState>(HabitOperationUiState.Idle)
    val uiState: StateFlow<HabitOperationUiState> = _uiState.asStateFlow()
    // ADD Habit uiState
    private val _addHabitUiState = MutableStateFlow(AddHabitUiState())
    val addHabitUiState: StateFlow<AddHabitUiState> = _addHabitUiState.asStateFlow()

    // Estado para controlar o filtro aplicado
    private val _habitFilter = MutableStateFlow(HabitFilter.PENDING)
    val habitFilter: StateFlow<HabitFilter> = _habitFilter.asStateFlow()
    private val dateFormatter = DateTimeFormatter.ISO_DATE
    private val today = LocalDate.now().format(dateFormatter)
    private val userIdFlow: Flow<Long?> =
        userPreferences.userFlow.map { it?.id }

    private val _completedTodayMap = MutableStateFlow<Map<Long, Boolean>>(emptyMap())
    val completedTodayMap: StateFlow<Map<Long, Boolean>> = _completedTodayMap

    // Armazena a contagem de hábitos
    val totalHabitsCount: StateFlow<Int> = userIdFlow
        .flatMapLatest { userId ->
            if (userId != null) repository.countHabits(userId)
            else flowOf(0)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val completedHabitsCount: StateFlow<Int> = userIdFlow
        .flatMapLatest { userId ->
            if (userId != null) repository.countCompletedToday(userId, today)
            else flowOf(0)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val pendingHabitsCount: StateFlow<Int> =
        combine(totalHabitsCount, completedHabitsCount) { total, completed ->
            (total - completed).coerceAtLeast(0)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    // Lista de Hábitos
    val habits: Flow<PagingData<Habit>> =
        combine(_habitFilter, userIdFlow) { filter, userId ->
            Pair(filter, userId)
        }.flatMapLatest { (filter, userId) ->
            if (userId == null) {
                flowOf(PagingData.empty())
            } else {
                when (filter) {
                    HabitFilter.ALL -> repository.getHabitsByUser(userId)
                    HabitFilter.COMPLETED -> repository.getCompletedHabits(userId, today)
                    HabitFilter.PENDING -> repository.getPendingHabits(userId, today)
                }
            }
        }.cachedIn(viewModelScope)

    fun setHabitFilter(filter: HabitFilter){
        _habitFilter.value = filter
    }
    // Funções CRUD
    fun submitHabit(onSucess: ()-> Unit){
        val state = _addHabitUiState.value

        if(state.name.isBlank()){
            _addHabitUiState.update {
                it.copy(isNameError = true)
            }
            return
        }
        // Adiciona no banco
        viewModelScope.launch {
            _uiState.value = HabitOperationUiState.Loading

            val userSession = userPreferences.userFlow.first()

            if (userSession == null) {
                // usuário não está logado
                return@launch
            }
            //  Cria o objeto que será adicionado ao banco
            val habit = Habit (
                id = state.habitId ?: 0L,
                name = state.name,
                userId = userSession.id,
                description = state.description,
                latitude = state.latitude,
                longitude = state.longitude,
                locationName = state.locationName
            )
            // Chama o repository e insere o novo hábito
            val result = if(state.habitId == null){
                repository.insertHabit(habit)
            }else{
                repository.updateHabit(habit)
            }


            // Atualiza o estado conforme o result
            _uiState.value = if (result.isSuccess){
                if(state.habitId == null){
                    HabitOperationUiState.HabitAdded
                }else{
                    HabitOperationUiState.HabitUpdated
                }

            } else {
                val erroMessage = result.exceptionOrNull()?.message ?: "Erro desconhecido"
                HabitOperationUiState.Error(erroMessage)
            }

            if(result.isSuccess){
                resetAddHabitForm()
                onSucess()
            }

        }
    }
    fun startEditHabit(habitId: Long) {
        viewModelScope.launch {
            val habit = repository.getHabitById(habitId) ?: return@launch

            _addHabitUiState.value = AddHabitUiState(
                habitId = habit.id,
                name = habit.name,
                description = habit.description,
                locationName = habit.locationName,
                latitude = habit.latitude,
                longitude = habit.longitude
            )
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

    fun onNameChange(value: String) {
        _addHabitUiState.update {
            it.copy(
                name = value,
                isNameError = value.isBlank()
            )
        }
    }

    fun onDescriptionChange(value: String) {
        _addHabitUiState.update {
            it.copy(description = value)
        }
    }

    fun onShowMapChange(show: Boolean) {
        _addHabitUiState.update {
            it.copy(showMapScreen = show)
        }
    }

    fun onLocationSelected(
        locationName: String?,
        latitude: Double?,
        longitude: Double?
    ) {
        _addHabitUiState.update {
            it.copy(
                locationName = locationName,
                latitude = latitude,
                longitude = longitude,
                showMapScreen = false
            )
        }
    }

    fun resetAddHabitForm() {
        _addHabitUiState.value = AddHabitUiState()
    }

    fun toggleHabitCompletion(habitId: Long) {
        viewModelScope.launch {
            val isCompleted = repository.isHabitCompleted(habitId, today).first()
            val userId = userIdFlow.filterNotNull().first()

            repository.toggleHabitCompletion(userId, isCompleted, habitId, today)

            _completedTodayMap.update { current ->
                current.toMutableMap().apply {
                    this[habitId] = !isCompleted
                }
            }
        }
    }
    fun isHabitCompletedTodayFlow(habitId: Long): Flow<Boolean>{
        return repository.isHabitCompleted(habitId, today)
    }

    fun resetState() {
        _uiState.value = HabitOperationUiState.Idle
    }

}