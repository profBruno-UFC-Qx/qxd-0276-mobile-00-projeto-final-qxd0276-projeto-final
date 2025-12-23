package com.pegai.app.ui.viewmodel.profile

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    // --- AÇÕES DE UI ---

    fun abrirPixDialog() {
        // Ao abrir, copiamos a chave atual para a temporária
        _uiState.update {
            it.copy(
                isPixDialogVisible = true,
                chavePixTemp = it.chavePix
            )
        }
    }

    fun fecharPixDialog() {
        _uiState.update { it.copy(isPixDialogVisible = false) }
    }

    fun atualizarChaveTemp(novaChave: String) {
        _uiState.update { it.copy(chavePixTemp = novaChave) }
    }

    fun salvarChavePix() {
        // TODO: Aqui entra a chamada ao Firestore para salvar de verdade
        val novaChave = _uiState.value.chavePixTemp

        _uiState.update {
            it.copy(
                chavePix = novaChave, // Confirma a alteração
                isPixDialogVisible = false
            )
        }
    }
}