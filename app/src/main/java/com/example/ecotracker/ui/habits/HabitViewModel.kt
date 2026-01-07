package com.example.ecotracker.ui.addHabit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.ecotracker.data.local.entity.Habit
import com.example.ecotracker.data.repository.HabitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class HabitUiState {
    object Idle : HabitUiState()
    object Success : HabitUiState()
    object Loading : HabitUiState()
    object HabitAdded : HabitUiState()
    object HabitUpdated : HabitUiState()
    object HabitDeleted : HabitUiState()
    data class Error(val msg: String) : HabitUiState()
}

class HabitViewModel(
    private val repository: HabitRepository
) : ViewModel() {
    val habits = repository.getAllHabits().cachedIn(viewModelScope)
    private val _uiState = MutableStateFlow<HabitUiState>(HabitUiState.Idle)
    val uiState: StateFlow<HabitUiState> = _uiState.asStateFlow()

    fun addHabit(
        name: String,
        userId : Long,
        description: String
    ){
        if(name.isBlank()){
            _uiState.value = HabitUiState.Error(
                "O nome do hábito é obrigatório"
            )
            return
        }
        viewModelScope.launch {
            _uiState.value = HabitUiState.Loading
            try {
                repository.insertHabit(
                    Habit(
                        name = name,
                        userId = userId,
                        description = description,
                        isCompleted = false
                    )
                )
                _uiState.value = HabitUiState.HabitAdded
            } catch (e: Exception) {
                _uiState.value = HabitUiState.Error("Erro ao salvar hábito")
            }
        }
    }
    fun updateHabit(habit: Habit) {
        viewModelScope.launch {
            _uiState.value = HabitUiState.Loading
            try{
                repository.updateHabit(habit)
                _uiState.value = HabitUiState.HabitUpdated
            }
            catch (e: Exception){
                _uiState.value = HabitUiState.Error("Erro ao atualizar hábito")
            }
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            _uiState.value = HabitUiState.Loading
            try{
                repository.deleteHabit(habit)
                _uiState.value = HabitUiState.HabitDeleted
            }
            catch(e: Exception) {
                _uiState.value = HabitUiState.Error("Erro ao deletar hábito")
            }
        }
    }

    fun resetState() {
        _uiState.value = HabitUiState.Idle
    }

}