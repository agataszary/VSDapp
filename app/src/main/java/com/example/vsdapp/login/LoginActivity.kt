package com.example.vsdapp.login

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
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
import org.koin.androidx.viewmodel.ext.android.viewModel


class LoginActivity : AppCompatActivity() {

    private val viewModel by viewModel<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent { LoginScreen() }
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
            isButtonEnabled = viewModel.isButtonEnabled.value
        )
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
    isButtonEnabled: Boolean
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
                        .padding(bottom = dimensionResource(R.dimen.margin_xlarge))
                ) {
                    Text(
                        text = stringResource(R.string.log_in),
                        modifier = Modifier
                            .padding(dimensionResource(R.dimen.margin_large))
                    )
                }
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
        isButtonEnabled = true
    )
}