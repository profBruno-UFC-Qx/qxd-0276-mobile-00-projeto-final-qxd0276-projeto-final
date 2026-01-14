package com.example.ecotracker.data.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

// Extension do DataStore
private val Context.dataStore by preferencesDataStore(name = "user_prefs")

object UserPreferencesKeys {
    val USER_ID = longPreferencesKey("user_id")
    val USER_NAME = stringPreferencesKey("user_name")
    val USER_EMAIL = stringPreferencesKey("user_email")
}

class UserPreferences(private val context: Context) {

    suspend fun saveUser(id: Long, name: String, email: String) {
        context.dataStore.edit { prefs ->
            prefs[UserPreferencesKeys.USER_ID] = id
            prefs[UserPreferencesKeys.USER_NAME] = name
            prefs[UserPreferencesKeys.USER_EMAIL] = email
        }
    }

    val userFlow: Flow<UserSession?> =
        context.dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { prefs ->
                val id = prefs[UserPreferencesKeys.USER_ID]
                val name = prefs[UserPreferencesKeys.USER_NAME]
                val email = prefs[UserPreferencesKeys.USER_EMAIL]

                if (id != null && name != null && email != null) {
                    UserSession(id, name, email)
                } else null
            }

    suspend fun clearUser() {
        context.dataStore.edit { it.clear() }
    }
}
