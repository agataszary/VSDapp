package com.example.vsdapp.settings

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.IconToggleButton
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
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
import androidx.compose.ui.window.Dialog
import com.example.vsdapp.R
import com.example.vsdapp.compose.GalleryTopNavBar
import com.example.vsdapp.compose.LoadingScreen
import com.example.vsdapp.compose.SegmentedButtons
import com.example.vsdapp.core.AppMode
import com.example.vsdapp.core.CloseActivity
import com.example.vsdapp.core.ShowToast
import com.example.vsdapp.core.ViewState
import com.example.vsdapp.core.runEventsCollector
import com.example.vsdapp.login.LoginActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity: AppCompatActivity() {

    companion object {
        fun start(activity: Activity) {
            val intent = Intent(activity, SettingsActivity::class.java)
            activity.startActivity(intent)
        }
    }

    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SettingsScreen()
        }

        setupEventsObserver()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                viewModel.onBackClicked()
            }
        })
    }

    private fun setupEventsObserver() {
        runEventsCollector(viewModel) { event ->
            when (val payload = event.getContent()) {
                is CloseActivity -> finish()
                is ShowToast -> showToast(payload.message)
            }
        }
    }

    private fun logOut() {
        Firebase.auth.signOut()
        LoginActivity.start(this)
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(
            this,
            message,
            Toast.LENGTH_SHORT,
        ).show()
    }

    @Composable
    fun SettingsScreen() {
        when (viewModel.viewStateFlow.collectAsState().value) {
            is ViewState.Content -> SettingsContent(
                onBackClicked = { viewModel.onBackClicked() },
                selectedAppMode = viewModel.appModeState.value,
                onAppModeButtonClicked = { viewModel.onAppModeButtonClicked(it) },
                onLogoutButtonClicked = { logOut() },
                mainNameValue = viewModel.mainNameValue.value,
                mainSurnameValue = viewModel.mainSurnameValue.value,
                childNameValue = viewModel.childNameValue.value,
                childSurnameValue = viewModel.childSurnameValue.value,
                onMainNameValueChanged = {value, isSurname -> viewModel.onMainNameChanged(value, isSurname) },
                onChildNameChanged = {value, isSurname -> viewModel.onChildNameChanged(value, isSurname) },
                emailAddressValue = viewModel.emailAddressValue.value,
                onEmailAddressValueChanged = { viewModel.onEmailAddressValueChanged(it) },
                isEmailError = viewModel.isEmailError.value,
                onEditDataButtonClicked = { viewModel.onEditDataClicked() },
                onEditPasswordButtonClicked = { viewModel.onEditPasswordClicked() },
                isEditing = viewModel.isEditMode.value,
                onCancelButtonClicked = { viewModel.onCancelButtonClicked() },
                showChangePasswordDialog = viewModel.showChangePasswordDialog.value,
                changeDialogState = { viewModel.changeDialogState(it) },
                oldPasswordValue = viewModel.oldPasswordValue.value,
                newPasswordValue = viewModel.newPasswordValue.value,
                onPasswordValueChanged = { value, isNew -> viewModel.onPasswordValueChanged(value, isNew) }
            )
            is ViewState.Progress -> LoadingScreen()
            else -> {}
        }
    }
}

@Composable
fun SettingsContent(
    onBackClicked: () -> Unit,
    selectedAppMode: AppMode,
    onAppModeButtonClicked: (Int) -> Unit,
    onLogoutButtonClicked: () -> Unit,
    mainNameValue: String,
    onMainNameValueChanged: (String, Boolean) -> Unit,
    mainSurnameValue: String,
    childNameValue: String,
    childSurnameValue: String,
    onChildNameChanged: (String, Boolean) -> Unit,
    emailAddressValue: String,
    onEmailAddressValueChanged: (String) -> Unit,
    isEmailError: Boolean,
    onEditDataButtonClicked: () -> Unit,
    onEditPasswordButtonClicked: () -> Unit,
    isEditing: Boolean,
    onCancelButtonClicked: () -> Unit,
    showChangePasswordDialog: Boolean,
    changeDialogState: (Boolean) -> Unit,
    oldPasswordValue: String,
    newPasswordValue: String,
    onPasswordValueChanged: (String, Boolean) -> Unit
) {
    val focusManager = LocalFocusManager.current

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = { GalleryTopNavBar(
            onBackClicked = { onBackClicked() },
            backButtonText = stringResource(R.string.top_nav_bar_back_arrow_text)
        ) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                }
        ){
            if (showChangePasswordDialog) {
                ChangePasswordDialog(
                    changeDialogState = changeDialogState,
                    focusManager = focusManager,
                    oldPasswordValue = oldPasswordValue,
                    newPasswordValue = newPasswordValue,
                    onPasswordValueChange = onPasswordValueChanged
                )
            }
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.Center)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = stringResource(R.string.app_mode)
                )
                SegmentedButtons(
                    firstButtonContent = stringResource(R.string.parental_mode),
                    secondButtonContent = stringResource(R.string.child_mode),
                    onButtonClicked = onAppModeButtonClicked,
                    selectedButton = selectedAppMode.toInt()
                )

                Card(
                    shape = RoundedCornerShape(10.dp),
                    elevation = 0.dp,
                    border = BorderStroke(1.dp, if (isEditing) colorResource(R.color.light_purple) else colorResource(R.color.gray)),
                    modifier = Modifier
                        .padding(
                            top = dimensionResource(R.dimen.margin_xlarge),
                            bottom = dimensionResource(R.dimen.margin_xlarge)
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .padding(
                                top = dimensionResource(R.dimen.margin_small),
                                bottom = dimensionResource(R.dimen.margin_small),
                                start = dimensionResource(R.dimen.margin_large),
                                end = dimensionResource(R.dimen.margin_large)
                            )
                    ) {
                        MainNameAndSurnameSection(
                            mainNameValue = mainNameValue,
                            mainSurnameValue = mainSurnameValue,
                            onMainNameValueChanged = onMainNameValueChanged,
                            focusManager = focusManager,
                            fieldsEnabled = isEditing
                        )
                        ChildNameAndSurnameSection(
                            childNameValue = childNameValue,
                            childSurnameValue = childSurnameValue,
                            onChildNameChanged = onChildNameChanged,
                            focusManager = focusManager,
                            fieldsEnabled = isEditing
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
                            enabled = isEditing,
                            modifier = Modifier
                                .padding(bottom = dimensionResource(R.dimen.margin_large))
                        )
                        Row(
                            modifier = Modifier
                                .width(IntrinsicSize.Max)
                        ) {
                            OutlinedButton(
                                onClick = { onEditDataButtonClicked() },
                                shape = RoundedCornerShape(40.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = colorResource(R.color.dark_purple_gray)
                                ),
                                border = BorderStroke(width = 2.dp, color = colorResource(R.color.nav_bar_background)),
                                modifier = Modifier
                                    .width(dimensionResource(R.dimen.settings_buttons_width))
                            ) {
                                Text(
                                    text = if (isEditing) stringResource(R.string.save_button_title) else stringResource(R.string.edit_data)
                                )
                            }
                            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.margin_large)))
                            if (isEditing) {
                                OutlinedButton(
                                    onClick = { onCancelButtonClicked() },
                                    shape = RoundedCornerShape(40.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = colorResource(R.color.dark_purple_gray)
                                    ),
                                    border = BorderStroke(width = 2.dp, color = colorResource(R.color.nav_bar_background)),
                                    modifier = Modifier
                                        .width(dimensionResource(R.dimen.settings_buttons_width))
                                ) {
                                    Text(
                                        text = stringResource(R.string.delete_scene_alert_cancel)
                                    )
                                }
                                Spacer(modifier = Modifier.width(dimensionResource(R.dimen.margin_large)))
                            }
                            OutlinedButton(
                                onClick = { onEditPasswordButtonClicked() },
                                shape = RoundedCornerShape(40.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = colorResource(R.color.dark_purple_gray)
                                ),
                                border = BorderStroke(width = 2.dp, color = colorResource(R.color.nav_bar_background)),
                                modifier = Modifier
                                    .width(dimensionResource(R.dimen.settings_buttons_width))
                            ) {
                                Text(
                                    text = stringResource(R.string.change_password)
                                )
                            }
                        }
                    }
                }

                OutlinedButton(
                    onClick = { onLogoutButtonClicked() },
                    shape = RoundedCornerShape(40.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = colorResource(R.color.red_x)
                    ),
                    border = BorderStroke(width = 1.dp, color = colorResource(R.color.red_x))
                ) {
                    Text(
                        text = stringResource(R.string.log_out_label),
                        modifier = Modifier
                            .padding(dimensionResource(R.dimen.margin_small))
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
    focusManager: FocusManager,
    fieldsEnabled: Boolean
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
            enabled = fieldsEnabled,
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
            enabled = fieldsEnabled
        )
    }
}

@Composable
private fun ChildNameAndSurnameSection(
    childNameValue: String,
    childSurnameValue: String,
    onChildNameChanged: (String, Boolean) -> Unit,
    focusManager: FocusManager,
    fieldsEnabled: Boolean
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
            enabled = fieldsEnabled,
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
            enabled = fieldsEnabled,
        )
    }
}

@Composable
private fun ChangePasswordDialog(
    changeDialogState: (Boolean) -> Unit,
    oldPasswordValue: String,
    newPasswordValue: String,
    onPasswordValueChange: (String, Boolean) -> Unit,
    focusManager: FocusManager
) {
    Dialog(
        onDismissRequest = { changeDialogState(false) }
    ) {
        Card(
            shape = RoundedCornerShape(10.dp),
            backgroundColor = Color.White,
            modifier = Modifier
                .width(300.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.margin_large))
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.change_password),
                        fontSize = 20.sp,
                        modifier = Modifier
                            .padding(bottom = dimensionResource(R.dimen.margin_large))
                    )
                    OutlinedTextField(
                        value = oldPasswordValue,
                        onValueChange = { onPasswordValueChange(it, false) },
                        label = {
                            Text(
                                text = stringResource(R.string.old_password)
                            )
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        ),
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier
                            .padding(bottom = dimensionResource(id = R.dimen.margin_large))
                    )
                    OutlinedTextField(
                        value = newPasswordValue,
                        onValueChange = { onPasswordValueChange(it, true) },
                        label = {
                            Text(
                                text = stringResource(R.string.new_password)
                            )
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        ),
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier
                            .padding(bottom = dimensionResource(id = R.dimen.margin_large))
                    )
                }
                Row {
                    TextButton(
                        onClick = { changeDialogState(false) }
                    ) {
                        Text(text = stringResource(R.string.delete_scene_alert_cancel))
                    }
                    Spacer(modifier = Modifier.width(dimensionResource(R.dimen.margin_small)))
                    TextButton(
                        onClick = { changeDialogState(true) }
                    ) {
                        Text(text = stringResource(R.string.save_button_title))
                    }
                }
            }
        }
    }
}

@Composable
@Preview(
    widthDp = 800
)
private fun SettingsContentPreview() {
    SettingsContent(
        onBackClicked = {},
        onAppModeButtonClicked = {},
        selectedAppMode = AppMode.PARENTAL_MODE,
        onLogoutButtonClicked = {},
        mainNameValue = "Anna",
        onMainNameValueChanged = {_,_ ->},
        mainSurnameValue = "Kowalska",
        childNameValue = "Jaś",
        childSurnameValue = "Kowalski",
        onChildNameChanged = {_,_->},
        emailAddressValue = "aaa@bbb.com",
        onEmailAddressValueChanged = {},
        isEmailError = false,
        onEditDataButtonClicked = {},
        onEditPasswordButtonClicked = {},
        isEditing = false,
        onCancelButtonClicked = {},
        showChangePasswordDialog = true,
        changeDialogState = {},
        oldPasswordValue = "123456",
        newPasswordValue = "1234",
        onPasswordValueChanged = {_,_->}
    )
}