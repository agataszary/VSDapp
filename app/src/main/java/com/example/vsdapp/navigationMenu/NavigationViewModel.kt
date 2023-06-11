package com.example.vsdapp.navigationMenu

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.vsdapp.core.AppMode
import com.example.vsdapp.core.ComposeViewModel
import com.example.vsdapp.core.PreferencesDataStore
import kotlinx.coroutines.launch

class NavigationViewModel: ComposeViewModel() {

    var appMode = mutableStateOf(AppMode.NONE)

    fun loadData(){
        showProgress()
        viewModelScope.launch {
            appMode.value = dataStore.getPreference(PreferencesDataStore.APP_MODE_KEY)
            println("$appMode")
            showContent()
        }

    }
}