package com.example.vsdapp.register

import android.util.Patterns
import androidx.compose.runtime.mutableStateOf
import com.example.vsdapp.core.ComposeViewModel

class RegisterViewModel : ComposeViewModel() {

    var emailAddressValue = mutableStateOf("")
        private set
    var isEmailError = mutableStateOf(false)
        private set
    var mainNameValue = mutableStateOf("")
        private set
    var mainSurnameValue = mutableStateOf("")
        private set
    var childNameValue = mutableStateOf("")
        private set
    var childSurnameValue = mutableStateOf("")
        private set
    var passwordValue = mutableStateOf("")
        private set
    var repeatPasswordValue = mutableStateOf("")
        private set
    var therapistAccountIsChecked = mutableStateOf(false)
        private set
    var isPasswordError = mutableStateOf(false)
        private set
    var isButtonEnabled = mutableStateOf(false)
        private set
        get() = mutableStateOf(checkFields())
    var openAlertDialog = mutableStateOf(false)
        private set

    fun onEmailAddressValueChanged(value: String) {
        emailAddressValue.value = value
        isEmailError.value = !Patterns.EMAIL_ADDRESS.matcher(value).matches()
    }

    fun onMainNameChanged(value: String, isItSurname: Boolean) {
        if (isItSurname) mainSurnameValue.value = value else mainNameValue.value = value
    }

    fun onChildNameChanged(value: String, isItSurname: Boolean) {
        if (isItSurname) childSurnameValue.value = value else childNameValue.value = value
    }

    fun onPasswordValueChanged(value: String, isItRepeat: Boolean) {
        if (isPasswordError.value) isPasswordError.value = false
        if (isItRepeat) repeatPasswordValue.value = value else passwordValue.value = value
    }

    fun onTherapistAccountCheckChanged(isChecked: Boolean) {
        therapistAccountIsChecked.value = isChecked
        if (isChecked) openAlertDialog.value = true
    }

    fun onRegisterButtonClicked() {
        if (passwordValue.value != repeatPasswordValue.value) isPasswordError.value = true
    }

    private fun checkFields(): Boolean {
        return mainNameValue.value.isNotBlank() && mainSurnameValue.value.isNotBlank() && passwordValue.value.isNotEmpty() && repeatPasswordValue.value.isNotEmpty()
                && emailAddressValue.value.isNotBlank() && !isEmailError.value
    }

    fun changeAlertDialogState(state: Boolean){
        openAlertDialog.value = false
        if (!state) therapistAccountIsChecked.value = false
    }
}