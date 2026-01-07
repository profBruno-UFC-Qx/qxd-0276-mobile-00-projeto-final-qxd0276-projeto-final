package com.pegai.app.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pegai.app.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    // Instâncias do Firebase
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Estado do Usuário Global (Acessível por todo o app)
    private val _usuarioLogado = MutableStateFlow<User?>(null)
    val usuarioLogado: StateFlow<User?> = _usuarioLogado.asStateFlow()

    // --- BLOCO DE INICIALIZAÇÃO  ---
    // Assim que o app abre, ele verifica se já existe alguém logado no cache do Firebase
    init {
        verificarLoginSalvo()
    }

    // --- FUNÇÕES DE AÇÃO ---

    fun setUsuarioLogado(user: User) {
        _usuarioLogado.value = user
    }

    fun logout() {
        // Avisa o Firebase para destruir a sessão
        auth.signOut()

        // Limpa o estado local para a UI reagir (voltar para "Visitante")
        _usuarioLogado.value = null
    }

    // --- LÓGICA INTERNA ---

    private fun verificarLoginSalvo() {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            // Se o Firebase diz que tem alguém logado, vamos buscar os dados dele no Banco
            // (Nome, Foto etc) para preencher a tela
            val uid = currentUser.uid

            db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val user = document.toObject(User::class.java)
                        if (user != null) {
                            _usuarioLogado.value = user.copy(uid = uid)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("AuthViewModel", "Erro ao recuperar dados do usuário: ${e.message}")
                    // Se falhar (ex: sem internet), mantém logado mas sem os dados completos por enquanto
                }
        }
    }
}