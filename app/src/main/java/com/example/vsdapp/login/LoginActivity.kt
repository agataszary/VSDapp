package com.example.vsdapp.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vsdapp.R
import com.example.vsdapp.core.OpenNavigationActivity
import com.example.vsdapp.core.OpenRegisterActivity
import com.example.vsdapp.core.ShowToast
import com.example.vsdapp.core.runEventsCollector
import com.example.vsdapp.navigationMenu.NavigationActivity
import com.example.vsdapp.register.RegisterActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.koin.androidx.viewmodel.ext.android.viewModel


class LoginActivity : AppCompatActivity() {

    companion object {
        fun start(activity: Activity){
            val intent = Intent(activity, LoginActivity::class.java)
            activity.startActivity(intent)
        }
    }

    private val viewModel by viewModel<LoginViewModel>()
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        val currentUser = auth.currentUser

        if(currentUser != null)  {
            println(currentUser.email)
            openNavigationActivity()
        } else {
            setContent { LoginScreen() }
        }

        setupEventsObserver()
    }

    @Composable
    private fun LoginScreen() {
        LoginContent(
            emailAddressValue = viewModel.emailAddressValue.value,
            onEmailAddressValueChanged = { viewModel.onEmailAddressValueChanged(it) },
            isEmailError = viewModel.isEmailError.value,
            passwordValue = viewModel.passwordValue.value,
            onPasswordValueChanged = { viewModel.onPasswordValueChanged(it) },
            onLoginButtonClicked = { viewModel.onLoginButtonClicked() },
            onCreateAccountClicked = { viewModel.onCreateAccountClicked() },
            isButtonEnabled = viewModel.isButtonEnabled.value,
            onDontRememberButtonClicked = { viewModel.onDontRememberPasswordClicked() },
            openResetPasswordDialog = viewModel.resetPasswordDialogVisible.value,
            changeAlertDialogState = { viewModel.changeAlertDialogState(it) },
            resetEmailValue = viewModel.resetEmailValue.value,
            onResetEmailValueChange = { viewModel.onResetEmailValueChange(it) }
        )
    }

    private fun setupEventsObserver() {
        runEventsCollector(viewModel) { event ->
            when (val payload = event.getContent()) {
                is OpenNavigationActivity -> openNavigationActivity()
                is ShowToast -> showErrorToast(payload.message)
                is OpenRegisterActivity -> openRegisterActivity()
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
            Toast.LENGTH_LONG,
        ).show()
    }

    private fun openRegisterActivity() {
        RegisterActivity.start(this)
        finish()
    }

}

@Composable
fun LoginContent(
    emailAddressValue: String,
    onEmailAddressValueChanged: (String) -> Unit,
    isEmailError: Boolean,
    passwordValue: String,
    onPasswordValueChanged: (String) -> Unit,
    onLoginButtonClicked: () -> Unit,
    onCreateAccountClicked: () -> Unit,
    isButtonEnabled: Boolean,
    onDontRememberButtonClicked: () -> Unit,
    openResetPasswordDialog: Boolean,
    changeAlertDialogState: (Boolean) -> Unit,
    resetEmailValue: String,
    onResetEmailValueChange: (String) -> Unit
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
            if (openResetPasswordDialog) {
                ResetPasswordDialog(
                    changeAlertDialogState = changeAlertDialogState,
                    resetEmailValue = resetEmailValue,
                    onResetEmailValueChange = onResetEmailValueChange
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 100.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = stringResource(R.string.log_in),
                    fontSize = 32.sp,
                    color = Color.Black,
                    modifier = Modifier
                        .padding(bottom = dimensionResource(R.dimen.margin_xlarge))
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
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    modifier = Modifier
                        .padding(bottom = dimensionResource(R.dimen.margin_xlarge))
                )
                OutlinedTextField(
                    value = passwordValue,
                    onValueChange = { onPasswordValueChanged(it) },
                    label = {
                        Text(
                            text = stringResource(R.string.password)
                        )
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    modifier = Modifier
                        .padding(bottom = dimensionResource(R.dimen.margin_xlarge))
                )
                Button(
                    onClick = {
                        onLoginButtonClicked()
                        focusManager.clearFocus()
                    },
                    shape = RoundedCornerShape(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = colorResource(R.color.medium_purple),
                        contentColor = Color.White
                    ),
                    enabled = isButtonEnabled,
                    modifier = Modifier
                        .padding(bottom = dimensionResource(R.dimen.margin_large))
                ) {
                    Text(
                        text = stringResource(R.string.log_in),
                        modifier = Modifier
                            .padding(dimensionResource(R.dimen.margin_large))
                    )
                }
                ClickableText(
                    text = AnnotatedString(stringResource(R.string.dont_remember_password)),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.medium_purple)
                    ),
                    onClick = {
                        onDontRememberButtonClicked()
                        focusManager.clearFocus()
                    },
                    modifier = Modifier
                        .padding(bottom = dimensionResource(R.dimen.margin_large))
                )
                Row {
                    Text(
                        text = stringResource(R.string.login_view_question),
                        modifier = Modifier
                            .padding(end = dimensionResource(R.dimen.margin_tiny))
                    )
                    ClickableText(
                        text = AnnotatedString(stringResource(R.string.create_account)),
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            color = colorResource(R.color.medium_purple)
                        ),
                        onClick = {
                            onCreateAccountClicked()
                            focusManager.clearFocus()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ResetPasswordDialog(
    changeAlertDialogState: (Boolean) -> Unit,
    resetEmailValue: String,
    onResetEmailValueChange: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = { changeAlertDialogState(false) },
        title = {
            Text(
                text = stringResource(R.string.dont_remember_password_alert_title),
                fontSize = 20.sp,
//                modifier = Modifier
//                    .padding(bottom = dimensionResource(R.dimen.margin_large))
            )
        },
        text = {
            OutlinedTextField(
                value = resetEmailValue,
                onValueChange = { onResetEmailValueChange(it) },
                label = {
                    Text(
                        text = stringResource(R.string.email_address)
                    )
                },
//                modifier = Modifier
//                    .padding(top = dimensionResource(R.dimen.margin_large))
            )
        },
        confirmButton = {
            TextButton(
                onClick = { changeAlertDialogState(true) }
            ) {
                Text(
                    text = stringResource(R.string.reset_label)
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

@Preview
@Composable
private fun LoginContentPreview() {
    LoginContent(
        emailAddressValue = "aaaa@bbbb.com",
        onEmailAddressValueChanged = {},
        isEmailError = true,
        passwordValue = "1234567890",
        onPasswordValueChanged = {},
        onLoginButtonClicked = {},
        onCreateAccountClicked = {
            println("dupa")
        },
        isButtonEnabled = true,
        onDontRememberButtonClicked = {},
        openResetPasswordDialog = true,
        onResetEmailValueChange = {},
        resetEmailValue = "aaa@mail.com",
        changeAlertDialogState = {}
    )
}