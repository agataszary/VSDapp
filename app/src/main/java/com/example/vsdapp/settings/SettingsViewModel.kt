package com.example.vsdapp.settings

import android.util.Patterns
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.vsdapp.core.AppMode
import com.example.vsdapp.core.CloseActivity
import com.example.vsdapp.core.ComposeViewModel
import com.example.vsdapp.core.Failure
import com.example.vsdapp.core.PreferencesDataStore
import com.example.vsdapp.core.QueryStatus
import com.example.vsdapp.core.ShowToast
import com.example.vsdapp.core.Success
import com.example.vsdapp.models.UserModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsViewModel(private val repository: SettingsRepository): ComposeViewModel() {

    var appModeState = mutableStateOf(AppMode.NONE)
        private set
    var mainNameValue = mutableStateOf("")
        private set
    var mainSurnameValue = mutableStateOf("")
        private set
    var childNameValue = mutableStateOf("")
        private set
    var childSurnameValue = mutableStateOf("")
        private set
    var emailAddressValue = mutableStateOf("")
        private set
    var isEmailError = mutableStateOf(false)
        private set
    var isEditMode = mutableStateOf(false)
        private set
    var showChangePasswordDialog = mutableStateOf(false)
        private set
    var oldPasswordValue = mutableStateOf("")
        private set
    var newPasswordValue = mutableStateOf("")
        private set
    var userModel: UserModel? = null

    init {
        showProgress()
        viewModelScope.launch {
            appModeState.value = withContext(Dispatchers.IO){ dataStore.getPreference(PreferencesDataStore.APP_MODE_KEY) }
            userModel = withContext(Dispatchers.IO){ repository.getUserData() }
            if (userModel == null) userModel = UserModel()
            userModel?.let { writeDataToFields(it) }
            println(userModel)
            showContent()
        }
    }

     private fun writeDataToFields(userModel: UserModel) {
        mainNameValue.value = userModel.mainName
        mainSurnameValue.value = userModel.mainSurname
        childNameValue.value = userModel.childName ?: ""
        childSurnameValue.value = userModel.childSurname ?: ""
        emailAddressValue.value = userModel.emailAddress
    }

    fun onBackClicked() {
        viewModelScope.launch {
            dataStore.updatePreference(PreferencesDataStore.APP_MODE_KEY, appModeState.value)
            println(appModeState.value)
            sendEvent(CloseActivity)
        }
    }

    fun onAppModeButtonClicked(index: Int) {
        appModeState.value = AppMode.fromValue(index)
    }

    fun onMainNameChanged(value: String, isItSurname: Boolean) {
        if (isItSurname) mainSurnameValue.value = value else mainNameValue.value = value
    }

    fun onChildNameChanged(value: String, isItSurname: Boolean) {
        if (isItSurname) childSurnameValue.value = value else childNameValue.value = value
    }

    fun onEmailAddressValueChanged(value: String) {
        emailAddressValue.value = value
        isEmailError.value = !Patterns.EMAIL_ADDRESS.matcher(value).matches()
    }

    fun onEditDataClicked() {
        if (isEditMode.value) {
            saveData()
            isEditMode.value = false
        } else {
            isEditMode.value = true
        }
    }

    fun onEditPasswordClicked() {
        showChangePasswordDialog.value = true
    }

    private fun saveData() {
        val updatedData = userModel?.copy(
            mainName = mainNameValue.value,
            mainSurname = mainSurnameValue.value,
            childName = childNameValue.value,
            childSurname = childSurnameValue.value,
            emailAddress = emailAddressValue.value
        )
        repository.updateUserData(updatedData ?: UserModel())
            .addOnSuccessListener {
                sendEvent(ShowToast("Zmiany zostały zapisane"))
                userModel = updatedData
            }
            .addOnFailureListener {
                sendEvent(ShowToast("Zapisanie zmian nie powiodło się, spróbuj ponownie"))
                userModel?.let { writeDataToFields(it) }
            }
    }

    fun onCancelButtonClicked() {
        isEditMode.value = false
        userModel?.let { writeDataToFields(it) }
    }

    fun changeDialogState(state: Boolean) {
        showChangePasswordDialog.value = state

        if (state){
            when (val queryResponse = repository.updateUserPassword(oldPasswordValue.value, newPasswordValue.value, userModel)) {
                is Success -> {
                    queryResponse.task
                        .addOnSuccessListener { sendEvent(ShowToast("Zmiana hasła zakończona pomyślnie")) }
                        .addOnFailureListener { sendEvent(ShowToast("Zmiana hasła nie powiodła się, spróbuj jeszcze raz")) }
                }
                is Failure -> {
                    sendEvent(ShowToast(queryResponse.message ?: "Zmiana hasła nie powiodła się, spróbuj jeszcze raz"))
                }
                else -> {}
            }
            showChangePasswordDialog.value = false
        }
    }

    fun onPasswordValueChanged(value: String, isNew: Boolean) {
        if (isNew) newPasswordValue.value = value else oldPasswordValue.value = value
    }
}