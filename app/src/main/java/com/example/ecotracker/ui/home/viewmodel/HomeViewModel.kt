package com.example.ecotracker.ui.home.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecotracker.data.repository.QuoteRepository
import com.example.ecotracker.utils.TranslatorHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val tipsRepository: QuoteRepository = QuoteRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadDailyPhrase()
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

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    motivationalPhrase = "Continue firme. Pequenas ações geram grandes resultados 🌱"
                )
            }
        }
    }

}
