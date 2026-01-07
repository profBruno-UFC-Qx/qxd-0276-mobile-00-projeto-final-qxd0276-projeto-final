package com.pegai.app.model

data class UserAvaliacao(
    val avalId: String = "",
    val userID: String = "",
    val nota: Int = 0,
    val comentario: String = "",
    val data: com.google.firebase.Timestamp? = null
)
