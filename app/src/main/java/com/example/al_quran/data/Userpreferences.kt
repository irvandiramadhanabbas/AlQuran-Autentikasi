package com.example.al_quran.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {
    companion object {
        val NAME_KEY = stringPreferencesKey("user_name")
        val EMAIL_KEY = stringPreferencesKey("user_email")
    }

    val userName = context.dataStore.data.map { it[NAME_KEY] ?: "" }
    val userEmail = context.dataStore.data.map { it[EMAIL_KEY] ?: "" }

    suspend fun saveUser(name: String, email: String) {
        context.dataStore.edit { prefs ->
            prefs[NAME_KEY] = name
            prefs[EMAIL_KEY] = email
        }
    }

    suspend fun clearUser() {
        context.dataStore.edit { it.clear() }
    }
}
