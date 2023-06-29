package com.example.vsdapp.login

import android.util.Patterns
import androidx.compose.runtime.mutableStateOf
import com.example.vsdapp.core.ComposeViewModel
import com.example.vsdapp.core.OpenNavigationActivity
import com.example.vsdapp.core.OpenRegisterActivity
import com.example.vsdapp.core.ShowToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

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
    var resetPasswordDialogVisible = mutableStateOf(false)
        private set
    var resetEmailValue = mutableStateOf("")
        private set

    var auth : FirebaseAuth = Firebase.auth

    fun onEmailAddressValueChanged(value: String) {
        emailAddressValue.value = value
        isEmailError.value = !Patterns.EMAIL_ADDRESS.matcher(value).matches()
    }

    fun onPasswordValueChanged(value: String) {
        passwordValue.value = value
    }

    fun onLoginButtonClicked() {
        auth.signInWithEmailAndPassword(emailAddressValue.value, passwordValue.value)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    println(user?.email)
                    sendEvent(OpenNavigationActivity)
                } else {
                    sendEvent(ShowToast("Niepoprawny e-mail lub hasło"))
                    isEmailError.value = true
                }
            }
    }

    fun onCreateAccountClicked() {
        sendEvent(OpenRegisterActivity)
    }

    fun onDontRememberPasswordClicked() {
        resetPasswordDialogVisible.value = true
    }

    fun changeAlertDialogState(state: Boolean){
        resetPasswordDialogVisible.value = false
        if (state && resetEmailValue.value.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(resetEmailValue.value).matches()) sendPasswordResetEmail()
    }

    private fun sendPasswordResetEmail() {
        Firebase.auth.sendPasswordResetEmail(resetEmailValue.value)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    sendEvent(ShowToast("Sprawdź swoją skrzynkę pocztową!"))
                }
            }
    }

    fun onResetEmailValueChange(value: String) {
        resetEmailValue.value = value
    }
}