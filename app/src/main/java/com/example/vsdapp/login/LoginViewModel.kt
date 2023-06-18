package com.example.vsdapp.login

import android.util.Patterns
import androidx.compose.runtime.mutableStateOf
import com.example.vsdapp.core.ComposeViewModel

class LoginViewModel : ComposeViewModel() {

    var emailAddressValue = mutableStateOf("")
        private set
    var passwordValue = mutableStateOf("")
        private set
    var isEmailError = mutableStateOf(false)
        private set
    var isButtonEnabled = mutableStateOf(false)
        private set
        get() = mutableStateOf(emailAddressValue.value.isNotBlank() && passwordValue.value.isNotEmpty() && !isEmailError.value)

    fun onEmailAddressValueChanged(value: String) {
        emailAddressValue.value = value
        isEmailError.value = !Patterns.EMAIL_ADDRESS.matcher(value).matches()
    }

    fun onPasswordValueChanged(value: String) {
        passwordValue.value = value
    }

    fun onLoginButtonClicked() {

    }

    fun onCreateAccountClicked() {

    }
}