package com.example.vsdapp.login

import android.util.Patterns
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.vsdapp.core.AppMode
import com.example.vsdapp.core.ComposeViewModel
import com.example.vsdapp.core.OpenNavigationActivity
import com.example.vsdapp.core.OpenRegisterActivity
import com.example.vsdapp.core.PreferencesDataStore
import com.example.vsdapp.core.ShowToast
import com.example.vsdapp.database.StorageRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(private val storageRepository: StorageRepository) : ComposeViewModel() {

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
                    checkForTherapistMode(user!!.uid)
                } else {
                    sendEvent(ShowToast("Niepoprawny e-mail lub hasło"))
                    isEmailError.value = true
                }
            }
    }

    private fun checkForTherapistMode(userId: String) {
        viewModelScope.launch(Dispatchers.Main){
            val userModel = withContext(Dispatchers.IO) { storageRepository.getUserDataForId(userId) }
            if (userModel != null && userModel.therapistAccount) {
                async { dataStore.updatePreference(PreferencesDataStore.APP_MODE_KEY, AppMode.THERAPIST_MODE) }.await()
            } else {
                async { dataStore.updatePreference(PreferencesDataStore.APP_MODE_KEY, AppMode.CHILD_MODE) }. await()
            }
            sendEvent(OpenNavigationActivity)
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