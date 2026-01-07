package com.pegai.app.ui.viewmodel.profile

import androidx.lifecycle.ViewModel
import com.pegai.app.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    // -------- USUÁRIO --------

    fun setUsuario(usuario: User?) {
        _uiState.update {
            it.copy(
                user = usuario,
                chavePix = usuario?.chavePix ?: ""
            )
        }
    }

    // -------- PIX --------

    fun abrirPixDialog() {
        _uiState.update {
            it.copy(
                isPixDialogVisible = true,
                chavePixTemp = it.chavePix
            )
        }
    }

    fun fecharPixDialog() {
        _uiState.update {
            it.copy(isPixDialogVisible = false)
        }
    }

    fun atualizarChaveTemp(novaChave: String) {
        _uiState.update {
            it.copy(chavePixTemp = novaChave)
        }
    }

    fun salvarChavePix() {
        val novaChave = _uiState.value.chavePixTemp

        // TODO: salvar no Firestore
        _uiState.update {
            it.copy(
                chavePix = novaChave,
                isPixDialogVisible = false
            )
        }
    }
}
