package com.example.vsdapp.register

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vsdapp.R
import com.example.vsdapp.compose.LoadingScreen
import com.example.vsdapp.core.CloseWithOkResult
import com.example.vsdapp.core.ShowToast
import com.example.vsdapp.core.ViewState
import com.example.vsdapp.core.runEventsCollector
import com.example.vsdapp.login.LoginActivity
import com.example.vsdapp.navigationMenu.NavigationActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterActivity : AppCompatActivity() {

    companion object {
        fun start(activity: Activity){
            val intent = Intent(activity, RegisterActivity::class.java)
            activity.startActivity(intent)
        }

        fun getIntent(activity: Activity): Intent {
           return Intent(activity, RegisterActivity::class.java)
        }
    }

    private val viewModel by viewModel<RegisterViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent { RegisterScreen() }

        setupEventsObserver()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                LoginActivity.start(this@RegisterActivity)
                finish()
            }
        })
    }

    private fun setupEventsObserver() {
        runEventsCollector(viewModel) { event ->
            when (val payload = event.getContent()) {
                is ShowToast -> showErrorToast(payload.message)
                is CloseWithOkResult -> openNavigationActivity()
            }
        }
    }

    private fun openNavigationActivity() {
        NavigationActivity.start(this)
        finish()
    }

    private fun showErrorToast(message: String) {
        Toast.makeText(
            this,
            message,
            Toast.LENGTH_SHORT,
        ).show()
    }

    @Composable
    fun RegisterScreen() {
        Crossfade(targetState = viewModel.viewStateFlow.collectAsState().value,
            label = ""
        ) { viewState ->
            when (viewState) {
                is ViewState.Progress -> LoadingScreen()
                is ViewState.Content -> {
                    RegisterContent(
                        emailAddressValue = viewModel.emailAddressValue.value,
                        onEmailAddressValueChanged = { viewModel.onEmailAddressValueChanged(it) },
                        isEmailError = viewModel.isEmailError.value,
                        mainNameValue = viewModel.mainNameValue.value,
                        onMainNameValueChanged = {value, isSurname -> viewModel.onMainNameChanged(value, isSurname) },
                        mainSurnameValue = viewModel.mainSurnameValue.value,
                        childNameValue = viewModel.childNameValue.value,
                        childSurnameValue = viewModel.childSurnameValue.value,
                        onChildNameChanged = {value, isSurname -> viewModel.onChildNameChanged(value, isSurname) },
                        passwordValue = viewModel.passwordValue.value,
                        repeatPasswordValue = viewModel.repeatPasswordValue.value,
                        onPasswordValueChanged = {value, isRepeat -> viewModel.onPasswordValueChanged(value, isRepeat) },
                        isPasswordError = viewModel.isPasswordError.value,
                        therapistAccountChecked = viewModel.therapistAccountIsChecked.value,
                        onTherapistAccountCheckChanged = { viewModel.onTherapistAccountCheckChanged(it) },
                        onRegisterButtonClicked = { viewModel.onRegisterButtonClicked() },
                        isButtonEnabled = viewModel.isButtonEnabled.value,
                        openAlertDialog = viewModel.openAlertDialog.value,
                        changeAlertDialogState = { viewModel.changeAlertDialogState(it) }
                    )
                }
                else -> {}
            }
        }
    }
}

@Composable
fun RegisterContent(
    emailAddressValue: String,
    onEmailAddressValueChanged: (String) -> Unit,
    isEmailError: Boolean,
    mainNameValue: String,
    onMainNameValueChanged: (String, Boolean) -> Unit,
    mainSurnameValue: String,
    childNameValue: String,
    childSurnameValue: String,
    onChildNameChanged: (String, Boolean) -> Unit,
    passwordValue: String,
    repeatPasswordValue: String,
    onPasswordValueChanged: (String, Boolean) -> Unit,
    isPasswordError: Boolean,
    therapistAccountChecked: Boolean,
    onTherapistAccountCheckChanged: (Boolean) -> Unit,
    onRegisterButtonClicked: () -> Unit,
    isButtonEnabled: Boolean,
    openAlertDialog: Boolean,
    changeAlertDialogState: (Boolean) -> Unit,
) {
    val focusManager = LocalFocusManager.current

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                }
        ) {
            if (openAlertDialog) {
                TherapistAlertDialog(
                    changeAlertDialogState = changeAlertDialogState
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 50.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = stringResource(R.string.register_account),
                    fontSize = 32.sp,
                    color = Color.Black,
                    modifier = Modifier
                        .padding(bottom = dimensionResource(R.dimen.margin_xlarge))
                )
                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    MainNameAndSurnameSection(
                        mainNameValue = mainNameValue,
                        mainSurnameValue = mainSurnameValue,
                        onMainNameValueChanged = onMainNameValueChanged,
                        focusManager = focusManager
                    )
                    ChildNameAndSurnameSection(
                        childNameValue = childNameValue,
                        childSurnameValue = childSurnameValue,
                        onChildNameChanged = onChildNameChanged,
                        focusManager = focusManager
                    )
                    OutlinedTextField(
                        value = emailAddressValue,
                        onValueChange = { onEmailAddressValueChanged(it) },
                        label = {
                            Text(
                                text = stringResource(R.string.email_address)
                            )
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        ),
                        isError = isEmailError,
                        modifier = Modifier
                            .padding(bottom = dimensionResource(R.dimen.margin_large))
                    )
                    PasswordSection(
                        passwordValue = passwordValue,
                        repeatPasswordValue = repeatPasswordValue,
                        onPasswordValueChanged = onPasswordValueChanged,
                        isPasswordError = isPasswordError,
                        focusManager = focusManager
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(bottom = dimensionResource(R.dimen.margin_large))
                    ) {
                        Checkbox(
                            checked = therapistAccountChecked,
                            onCheckedChange = { onTherapistAccountCheckChanged(it) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = colorResource(R.color.medium_purple)
                            )
                        )
                        Text(
                            text = stringResource(R.string.create_therapist_account_checkbox)
                        )
                    }
                }
                Button(
                    onClick = {
                        onRegisterButtonClicked()
                        focusManager.clearFocus()
                    },
                    shape = RoundedCornerShape(40.dp),
                    enabled = isButtonEnabled,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = colorResource(R.color.medium_purple),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = stringResource(R.string.register_account),
                        modifier = Modifier
                            .padding(dimensionResource(R.dimen.margin_large))
                    )
                }
            }
        }
    }
}

@Composable
private fun MainNameAndSurnameSection(
    mainNameValue: String,
    mainSurnameValue: String,
    onMainNameValueChanged: (String, Boolean) -> Unit,
    focusManager: FocusManager
) {
    Row(
        modifier = Modifier
            .padding(bottom = dimensionResource(R.dimen.margin_large))
    ) {
        OutlinedTextField(
            value = mainNameValue,
            onValueChange = { onMainNameValueChanged(it, false) },
            label = {
                Text(
                    text = stringResource(R.string.name_label)
                )
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
            modifier = Modifier
                .padding(end = dimensionResource(R.dimen.margin_small))
        )
        OutlinedTextField(
            value = mainSurnameValue,
            onValueChange = { onMainNameValueChanged(it, true) },
            label = {
                Text(
                    text = stringResource(R.string.surname_label)
                )
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
        )
    }
}

@Composable
private fun ChildNameAndSurnameSection(
    childNameValue: String,
    childSurnameValue: String,
    onChildNameChanged: (String, Boolean) -> Unit,
    focusManager: FocusManager
) {
    Row(
        modifier = Modifier
            .padding(bottom = dimensionResource(R.dimen.margin_large))
    ) {
        OutlinedTextField(
            value = childNameValue,
            onValueChange = { onChildNameChanged(it, false) },
            label = {
                Text(
                    text = stringResource(R.string.child_name_label_optional)
                )
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
            modifier = Modifier
                .padding(end = dimensionResource(R.dimen.margin_small))
        )
        OutlinedTextField(
            value = childSurnameValue,
            onValueChange = { onChildNameChanged(it, true) },
            label = {
                Text(
                    text = stringResource(R.string.child_surname_label_optional)
                )
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
        )
    }
}

@Composable
private fun PasswordSection(
    passwordValue: String,
    repeatPasswordValue: String,
    onPasswordValueChanged: (String, Boolean) -> Unit,
    isPasswordError: Boolean,
    focusManager: FocusManager
) {
    Row(
        modifier = Modifier
            .padding(bottom = dimensionResource(R.dimen.margin_large))
    ) {
        OutlinedTextField(
            value = passwordValue,
            onValueChange = { onPasswordValueChanged(it, false) },
            isError = isPasswordError,
            label = {
                Text(
                    text = stringResource(R.string.password)
                )
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .padding(end = dimensionResource(R.dimen.margin_small))
        )
        OutlinedTextField(
            value = repeatPasswordValue,
            onValueChange = { onPasswordValueChanged(it, true) },
            isError = isPasswordError,
            label = {
                Text(
                    text = stringResource(R.string.repeat_password)
                )
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
            visualTransformation = PasswordVisualTransformation()
        )
    }
}

@Composable
private fun TherapistAlertDialog(
    changeAlertDialogState: (Boolean) -> Unit
) {
    AlertDialog(
        onDismissRequest = { changeAlertDialogState(false) },
        title = {
            Text(
                text = stringResource(R.string.create_therapist_account_alert_title),
                fontSize = 20.sp
            )
        },
        text = {
            Text(
                text = stringResource(R.string.create_therapist_account_alert_body)
            )
        },
        confirmButton = {
            TextButton(
                onClick = { changeAlertDialogState(true) }
            ) {
                Text(
                    text = stringResource(R.string.delete_scene_alert_confirm)
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = { changeAlertDialogState(false) }
            ) {
                Text(
                    text = stringResource(R.string.delete_scene_alert_cancel)
                )
            }
        },
        modifier = Modifier
            .width(300.dp)
    )
}

@Preview(
    widthDp = 800
)
@Composable
private fun RegisterContentPreview() {
    RegisterContent(
        emailAddressValue = "aaa@bbb.com",
        onEmailAddressValueChanged = {},
        isEmailError = false,
        mainNameValue = "Anna",
        onMainNameValueChanged = {_,_ ->},
        mainSurnameValue = "Kowalska",
        passwordValue = "123456",
        onPasswordValueChanged = {_,_->},
        therapistAccountChecked = false,
        onTherapistAccountCheckChanged = {},
        onRegisterButtonClicked = {},
        isPasswordError = false,
        childNameValue = "Jaś",
        childSurnameValue = "Kowalski",
        onChildNameChanged = {_,_->},
        repeatPasswordValue = "123456",
        isButtonEnabled = true,
        openAlertDialog = false,
        changeAlertDialogState = {}
    )
}