package com.pegai.app.ui.viewmodel.register

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RegisterViewModel : ViewModel() {

    // Estado (MVVM)
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun cadastrarUsuario(
        nome: String,
        sobrenome: String,
        email: String,
        senha: String,
        telefone: String,
    ) {
        // Inicia loading
        _uiState.value = RegisterUiState(isLoading = true)

        // Criar Auth no Firebase
        auth.createUserWithEmailAndPassword(email, senha)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid
                if (uid != null) {
                    // Se Auth deu certo, salva no Firestore
                    salvarDadosNoFirestore(uid, nome, sobrenome, email, telefone)
                } else {
                    _uiState.value = RegisterUiState(isLoading = false, erro = "Erro ao gerar ID do usuário.")
                }
            }
            .addOnFailureListener { e ->
                // Tratamento de erros de cadastro
                val msg = when {
                    "email-already-in-use" in e.message.toString().lowercase() -> "Este e-mail já está cadastrado."
                    "weak-password" in e.message.toString().lowercase() -> "A senha deve ter pelo menos 6 caracteres."
                    "invalid-email" in e.message.toString().lowercase() -> "E-mail inválido."
                    else -> "Erro no cadastro: ${e.message}"
                }
                _uiState.value = RegisterUiState(isLoading = false, erro = msg)
            }
    }

    private fun salvarDadosNoFirestore(
        uid: String,
        nome: String,
        sobrenome: String,
        email: String,
        telefone: String,
    ) {
        val userMap = hashMapOf(
            "uid" to uid,
            "nome" to nome,
            "sobrenome" to sobrenome,
            "email" to email,
            "telefone" to telefone
        )

        db.collection("users").document(uid)
            .set(userMap)
            .addOnSuccessListener {
                _uiState.value = RegisterUiState(isLoading = false, cadastroSucesso = true)
            }
            .addOnFailureListener { e ->
                _uiState.value = RegisterUiState(isLoading = false, erro = "Erro ao salvar dados: ${e.message}")
            }
    }
}