package com.pegai.app.data.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.pegai.app.model.User
import com.pegai.app.model.UserAvaliacao
import kotlinx.coroutines.tasks.await

object UserRepository {
    private val cache = mutableMapOf<String, String>()
    private val cacheUsuarios = mutableMapOf<String, User>()
    private val avaliacoesCache = mutableMapOf<String, List<UserAvaliacao>>()


    suspend fun getNomeUsuario(userId: String): String {
        return cache[userId] ?: run {
            val doc = FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .get()
                .await()

            val nome = doc.getString("nome") ?: "Usuário"
            cache[userId] = nome
            nome
        }
    }

    suspend fun getUsuarioPorId(userId: String): User? {
        // Cache primeiro
        cacheUsuarios[userId]?.let { return it }

        val doc = FirebaseFirestore.getInstance()
            .collection("users")
            .document(userId)
            .get()
            .await()

        if (!doc.exists()) return null

        val usuario = doc.toObject(User::class.java)
        if (usuario != null) {
            cacheUsuarios[userId] = usuario
        }

        return usuario
    }

    suspend fun getAvaliacoesDoUsuario(userId: String): List<UserAvaliacao> {
        return avaliacoesCache[userId] ?: run {
            val snapshot = FirebaseFirestore.getInstance()
                .collection("userAval")
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val avaliacoes = snapshot.toObjects(UserAvaliacao::class.java)
            avaliacoesCache[userId] = avaliacoes
            avaliacoes
        }
    }
}