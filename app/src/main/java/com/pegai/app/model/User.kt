package com.pegai.app.model

/**
 * Representa um usuário cadastrado no Pegaí.
 */
data class User(
    val uid: String = "",
    val nome: String = "",
    val sobrenome: String = "",
    val email: String = "",
    val telefone: String = "",
    val fotoUrl: String = "",
    val chavePix: String = ""
)