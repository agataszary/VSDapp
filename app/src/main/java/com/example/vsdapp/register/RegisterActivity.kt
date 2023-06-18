package com.example.vsdapp.register

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vsdapp.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterActivity : AppCompatActivity() {

    private val viewModel by viewModel<RegisterViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent { RegisterScreen() }
    }

    @Composable
    fun RegisterScreen() {
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
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
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
                        onMainNameValueChanged = onMainNameValueChanged
                    )
                    ChildNameAndSurnameSection(
                        childNameValue = childNameValue,
                        childSurnameValue = childSurnameValue,
                        onChildNameChanged = onChildNameChanged
                    )
                    OutlinedTextField(
                        value = emailAddressValue,
                        onValueChange = { onEmailAddressValueChanged(it) },
                        label = {
                            Text(
                                text = stringResource(R.string.email_address)
                            )
                        },
                        isError = isEmailError,
                        modifier = Modifier
                            .padding(bottom = dimensionResource(R.dimen.margin_large))
                    )
                    PasswordSection(
                        passwordValue = passwordValue,
                        repeatPasswordValue = repeatPasswordValue,
                        onPasswordValueChanged = onPasswordValueChanged,
                        isPasswordError = isPasswordError
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
                    onClick = onRegisterButtonClicked,
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
    onMainNameValueChanged: (String, Boolean) -> Unit
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
        )
    }
}

@Composable
private fun ChildNameAndSurnameSection(
    childNameValue: String,
    childSurnameValue: String,
    onChildNameChanged: (String, Boolean) -> Unit
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
        )
    }
}

@Composable
private fun PasswordSection(
    passwordValue: String,
    repeatPasswordValue: String,
    onPasswordValueChanged: (String, Boolean) -> Unit,
    isPasswordError: Boolean
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