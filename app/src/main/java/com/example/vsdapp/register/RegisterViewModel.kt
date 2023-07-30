package com.example.vsdapp.register

import android.util.Patterns
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.vsdapp.core.AppMode
import com.example.vsdapp.core.CloseWithOkResult
import com.example.vsdapp.core.ComposeViewModel
import com.example.vsdapp.core.PreferencesDataStore
import com.example.vsdapp.core.ShowToast
import com.example.vsdapp.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest

class RegisterViewModel(private val repository: RegisterRepository) : ComposeViewModel() {

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

    var auth : FirebaseAuth = Firebase.auth

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

        if (!isPasswordError.value && !isEmailError.value) {
            showProgress()
            auth.createUserWithEmailAndPassword(emailAddressValue.value, passwordValue.value)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        if (user != null) {
                            val newUser = UserModel(
                                userId = user.uid,
                                mainName = mainNameValue.value,
                                mainSurname = mainSurnameValue.value,
                                childName = childNameValue.value,
                                childSurname = childSurnameValue.value,
                                emailAddress = emailAddressValue.value,
                                therapistAccount = therapistAccountIsChecked.value,
                                password = passwordValue.value,
                                savedStudents = if (therapistAccountIsChecked.value) listOf() else null
                            )
                            repository.addNewUser(newUser)
                                .addOnSuccessListener {
                                    if (newUser.therapistAccount) setTherapistMode() else sendEvent(CloseWithOkResult)
                                }
                                .addOnFailureListener {
                                    showContent()
                                    sendEvent(ShowToast("Rejestracja nie powiodła się, spróbuj ponownie"))
                                }
                        }
                    } else {
                        showContent()
                        sendEvent(ShowToast("Rejestracja nie powiodła się, spróbuj ponownie"))
                    }
                }
        }
    }

    private fun setTherapistMode() {
        viewModelScope.launch(Dispatchers.Main) {
            println("Therapist modeeeeeeeee")
            async {dataStore.updatePreference(PreferencesDataStore.APP_MODE_KEY, AppMode.THERAPIST_MODE)}.await()
            sendEvent(CloseWithOkResult)
        }
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