package com.example.projetopokedex.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.projetopokedex.data.model.UserLocal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

private const val USER_PREFS_NAME = "user_prefs"

private val Context.userDataStore by preferencesDataStore(
    name = USER_PREFS_NAME
)

object UserPrefsKeys {
    val AVATAR = stringPreferencesKey("avatar")
    val NAME = stringPreferencesKey("name")
    val EMAIL = stringPreferencesKey("email")
    val PASSWORD = stringPreferencesKey("password")
    val TOKEN = stringPreferencesKey("token")
}

class UserLocalDataSource(private val context: Context) {

    // USER

    suspend fun saveUser(user: UserLocal) {
        context.userDataStore.edit { prefs ->
            prefs[UserPrefsKeys.AVATAR] = user.avatar
            prefs[UserPrefsKeys.NAME] = user.name
            prefs[UserPrefsKeys.EMAIL] = user.email
            prefs[UserPrefsKeys.PASSWORD] = user.password
        }
    }

    suspend fun getUserByEmail(email: String): UserLocal? {
        val prefs = context.userDataStore.data.firstOrNull() ?: return null
        val storedEmail = prefs[UserPrefsKeys.EMAIL]
        if (storedEmail == null || storedEmail != email) return null

        val avatar = prefs[UserPrefsKeys.AVATAR] ?: ""
        val name = prefs[UserPrefsKeys.NAME] ?: ""
        val password = prefs[UserPrefsKeys.PASSWORD] ?: ""
        return UserLocal(
            avatar = avatar,
            name = name,
            email = storedEmail,
            password = password
        )
    }

    suspend fun getUserName(): String? {
        val prefs = context.userDataStore.data.firstOrNull() ?: return null
        return prefs[UserPrefsKeys.NAME]
    }

    // TOKEN

    val tokenFlow: Flow<String?> =
        context.userDataStore.data.map { prefs -> prefs[UserPrefsKeys.TOKEN] }

    suspend fun saveToken(token: String) {
        context.userDataStore.edit { prefs ->
            prefs[UserPrefsKeys.TOKEN] = token
        }
    }

    suspend fun clearToken() {
        context.userDataStore.edit { prefs ->
            prefs[UserPrefsKeys.TOKEN] = ""
        }
    }
}