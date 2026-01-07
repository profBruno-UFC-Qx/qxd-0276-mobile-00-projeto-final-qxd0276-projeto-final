package com.pegai.app.model

data class Avaliacao(
    val produtoId: String = "",
    val usuarioId: String = "",
    val nota: Int = 0,
    val descricao: String = "",
    val data: com.google.firebase.Timestamp? = null
)
