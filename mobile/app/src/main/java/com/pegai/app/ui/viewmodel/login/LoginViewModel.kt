package com.pegai.app.ui.viewmodel.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pegai.app.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    // Estado Privado (Só o ViewModel pode alterar)
    private val _uiState = MutableStateFlow(LoginUiState())

    // Estado Público (A Tela só pode ler ou observar)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun login(email: String, password: String) {
        // Avisa a tela que está carregando
        _uiState.value = _uiState.value.copy(isLoading = true, erro = null)

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val uid = auth.currentUser?.uid
                if (uid != null) {
                    verificarNoFirestore(uid)
                } else {
                    _uiState.value = LoginUiState(erro = "Erro ao recuperar ID do usuário.")
                }
            }
            .addOnFailureListener { e ->
                // Tratamento de erros
                val msg = when {
                    "password" in e.message.toString().lowercase() -> "Senha incorreta."
                    "no user record" in e.message.toString().lowercase() -> "Usuário não encontrado."
                    "badly formatted" in e.message.toString().lowercase() -> "E-mail inválido."
                    else -> "Erro: ${e.message}"
                }
                // (Falha): Avisa a tela do erro e para o loading
                _uiState.value = LoginUiState(isLoading = false, erro = msg)
            }
    }

    private fun verificarNoFirestore(uid: String) {
        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject(User::class.java)?.copy(uid = uid)
                    _uiState.value = LoginUiState(isLoading = false, loginSucesso = true, usuario = user)
                } else {
                    _uiState.value = LoginUiState(isLoading = false, erro = "Usuário sem cadastro no banco de dados.")
                }
            }
            .addOnFailureListener {
                _uiState.value = LoginUiState(isLoading = false, erro = "Erro de conexão com banco de dados.")
            }
    }

    // Função auxiliar para resetar o estado caso o usuário volte para a tela de login
    fun resetState() {
        _uiState.value = LoginUiState()
    }
}