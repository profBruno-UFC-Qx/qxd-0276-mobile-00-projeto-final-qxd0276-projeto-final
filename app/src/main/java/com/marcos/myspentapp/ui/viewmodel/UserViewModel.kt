package com.marcos.myspentapp.ui.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.marcos.myspentapp.ui.state.UserData

class UserViewModel : ViewModel() {

    var userState by mutableStateOf(UserData())
        private set

    fun updateName(newName: String) {
        userState = userState.copy(nome = newName)
    }

    fun updateEmail(newEmail: String) {
        userState = userState.copy(email = newEmail)
    }

    fun updateSenha(newSenha: String) {
        userState = userState.copy(senha = newSenha)
    }

    fun updateCode(newCode: String) {
        userState = userState.copy(codeRescue = newCode)
    }

    fun updatePhoto(uri: Uri?) {
        userState = userState.copy(fotoUri = uri)
    }

    fun updateGanhos(newGanhos: Double) {
        userState = userState.copy(ganhos = newGanhos)
    }

    fun toggleTheme() {
        userState = userState.copy(
            darkTheme = !userState.darkTheme,
            initApp = true
        )
    }

    fun setDarkTheme(enabled: Boolean) {
        if (!userState.initApp) {
            userState = userState.copy(
                darkTheme = enabled,
                initApp = true
            )
        }
    }

    fun deleteUser() {
        userState = UserData()
    }

}
