package com.example.vsdapp.core

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.concurrent.Flow

val Context.dataStore: DataStore<Preferences> by preferencesDataStore("PreferencesDataStore")

class PreferencesDataStore(context: Context) {

    companion object {
        val APP_MODE_KEY = intPreferencesKey("app_mode_key")
    }

    private val dataStore = context.dataStore

    suspend fun getPreference(key: Preferences.Key<Int>): AppMode {
        val dataFlow = dataStore.data.map { preferences ->
            preferences[APP_MODE_KEY] ?: 1
        }
        return AppMode.fromValue(dataFlow.first())
    }

    suspend fun updatePreference(key: Preferences.Key<Int>, value: AppMode) {
        dataStore.edit { preferences ->
            preferences[APP_MODE_KEY] = AppMode.toValue(value)
        }
    }
}