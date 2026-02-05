package com.example.ecotracker.ui.home.viewmodel

import android.util.Log
import androidx.compose.runtime.currentCompositionErrors
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecotracker.data.repository.QuoteRepository
import com.example.ecotracker.data.repository.UserRepository
import com.example.ecotracker.utils.TranslatorHelper
import com.example.ecotracker.utils.calculateLevel
import com.example.ecotracker.utils.calculateTreeStage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val tipsRepository: QuoteRepository = QuoteRepository(),
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState
    val userId = userRepository.getLoggedUserPreference().filterNotNull().map { it.id }

    init {
        loadDailyPhrase()
        observeUserProgress()
    }

    private fun loadDailyPhrase() {
        viewModelScope.launch {
            try {
                val quote = tipsRepository.getDailyQuote()
                TranslatorHelper.translateToPt(
                    text = quote.text,
                    onSuccess = { translated ->
                        _uiState.value = _uiState.value.copy(
                            motivationalPhrase = "“$translated” — ${quote.author}"
                        )
                    },
                    onError = {
                        _uiState.value = _uiState.value.copy(
                            motivationalPhrase = "“${quote.text}” — ${quote.author}"
                        )
                    }
                )
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false
                    )
                }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    motivationalPhrase = "Continue firme. Pequenas ações geram grandes resultados"
                )
            }
        }
    }
    private fun observeUserProgress(){
        viewModelScope.launch {
            userRepository.getUserPoints(userId.first()).collect { points ->
                val level = calculateLevel(points)
                val treeStage = calculateTreeStage(level)

                _uiState.update { currentState ->
                    currentState.copy(
                        points = points,
                        level = level,
                        treeStage = treeStage
                    )
                }
            }
        }
    }

}
