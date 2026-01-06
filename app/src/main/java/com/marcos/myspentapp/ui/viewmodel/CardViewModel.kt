package com.marcos.myspentapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import com.marcos.myspentapp.ui.state.CardUiState
import kotlinx.coroutines.flow.asStateFlow

class CardViewModel : ViewModel() {
    private val _cards = MutableStateFlow<List<CardUiState>>(emptyList())
    val cards = _cards.asStateFlow()
    private val _selectedIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedIds = _selectedIds.asStateFlow()

    fun addCard(card: CardUiState) {
        _cards.update { current ->
            current + card
        }
    }

    fun updateCard(id: String, updatedCard: CardUiState) {
        _cards.update { current ->
            current.map { card ->
                if (card.id == id) updatedCard else card
            }
        }
    }

    fun clear() {
        _cards.value = emptyList()
    }

    fun toggleSelection(id: String) {
        _selectedIds.value =
            if (_selectedIds.value.contains(id))
                _selectedIds.value - id
            else
                _selectedIds.value + id
    }

    fun clearSelection() {
        _selectedIds.value = emptySet()
    }

    fun removeSelected() {
        _cards.value = _cards.value.filterNot {
            _selectedIds.value.contains(it.id)
        }
        clearSelection()
    }
}








