package com.marcos.myspentapp.ui.state

import android.net.Uri

data class UserData(
    val nome: String = "Usuário",
    val email: String = "",
    val senha: String = "",
    val fotoUri: Uri? = null,
    val codeRescue: String = "",
    val ganhos: Double = 0.00,
    val darkTheme: Boolean = false,
    val initApp: Boolean = false
)
