package com.example.vsdapp.navigationMenu

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.vsdapp.core.AppMode
import com.example.vsdapp.core.AskForPassword
import com.example.vsdapp.core.ComposeViewModel
import com.example.vsdapp.core.OpenSettings
import com.example.vsdapp.core.PreferencesDataStore
import com.example.vsdapp.core.ShowToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NavigationViewModel(private val repository: NavigationRepository): ComposeViewModel() {

    var appMode = mutableStateOf(AppMode.NONE)
    var shouldShowAskForPasswordDialog = mutableStateOf(false)
        private set
    var passwordValue = mutableStateOf("")
        private set

    fun loadData(){
        showProgress()
        viewModelScope.launch {
            appMode.value = dataStore.getPreference(PreferencesDataStore.APP_MODE_KEY)
            println("$appMode")
            shouldShowAskForPasswordDialog.value = false
            passwordValue.value = ""
            showContent()
        }
    }

    fun onSettingsButtonClicked() {
        if (appMode.value == AppMode.CHILD_MODE) {
            shouldShowAskForPasswordDialog.value = true
        } else {
            sendEvent(OpenSettings)
        }
    }

    fun changeDialogState(state: Boolean) {
        if (state && passwordValue.value != "") {
            validatePassword()
        } else {
            shouldShowAskForPasswordDialog.value = false
            passwordValue.value = ""
        }
    }

    private fun validatePassword() {
        viewModelScope.launch(Dispatchers.Main) {
            val isPasswordValid = withContext(Dispatchers.IO){ repository.checkPassword(passwordValue.value) }
            if (isPasswordValid) {
                sendEvent(OpenSettings)
            } else {
                sendEvent(ShowToast("Niepoprawne hasło"))
            }
            shouldShowAskForPasswordDialog.value = false
            passwordValue.value = ""
        }
    }

    fun onPasswordValueChanged(value: String) {
        passwordValue.value = value
    }
}