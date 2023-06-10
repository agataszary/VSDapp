package com.example.vsdapp.settings

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.vsdapp.core.AppMode
import com.example.vsdapp.core.CloseActivity
import com.example.vsdapp.core.ComposeViewModel
import com.example.vsdapp.core.PreferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsViewModel: ComposeViewModel() {

    var appModeState = mutableStateOf(AppMode.NONE)
        private set

    init {
        showProgress()
        viewModelScope.launch {
            appModeState.value = withContext(Dispatchers.IO){ dataStore.getPreference(PreferencesDataStore.APP_MODE_KEY) }
        }
        showContent()
    }

    fun onBackClicked() {
        viewModelScope.launch {
            dataStore.updatePreference(PreferencesDataStore.APP_MODE_KEY, appModeState.value)
        }
        sendEvent(CloseActivity)
    }

    fun onAppModeButtonClicked(index: Int) {
        appModeState.value = AppMode.fromValue(index)
    }
}