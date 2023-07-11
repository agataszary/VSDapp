package com.example.vsdapp.navigationMenu

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.vsdapp.R
import com.example.vsdapp.compose.LoadingScreen
import com.example.vsdapp.compose.MenuButton
import com.example.vsdapp.core.AppMode
import com.example.vsdapp.core.AskForPassword
import com.example.vsdapp.core.CloseActivity
import com.example.vsdapp.core.Constants
import com.example.vsdapp.core.OpenSettings
import com.example.vsdapp.core.ShowToast
import com.example.vsdapp.core.ViewState
import com.example.vsdapp.core.runEventsCollector
import com.example.vsdapp.editMode.EditModeActivity
import com.example.vsdapp.editMode.EditModeType
import com.example.vsdapp.gallery.GalleryActivity
import com.example.vsdapp.readMode.ReadModeActivity
import com.example.vsdapp.settings.SettingsActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.koin.androidx.viewmodel.ext.android.viewModel

class NavigationActivity: AppCompatActivity() {

    companion object {
        fun start(activity: Activity) {
            val intent = Intent(activity, NavigationActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            activity.startActivity(intent)
        }
    }

    val viewModel by viewModel<NavigationViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.loadData()

        setContent {
            when (viewModel.viewStateFlow.collectAsState().value) {
                is ViewState.Content -> NavigationMenuScreen(
                    onGalleryButtonClicked = { openGalleryScreen() },
                    onEditModeButtonClicked = { openEditModeScreen() },
                    onSettingsButtonClicked = { viewModel.onSettingsButtonClicked() },
                    appMode = viewModel.appMode.value,
                    shouldShowAskForPasswordDialog = viewModel.shouldShowAskForPasswordDialog.value,
                    changeDialogState = { viewModel.changeDialogState(it) },
                    passwordValue = viewModel.passwordValue.value,
                    onPasswordValueChange = { viewModel.onPasswordValueChanged(it) }
                )
                is ViewState.Progress -> LoadingScreen()
                else -> {}
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if (viewModel.appMode.value != AppMode.CHILD_MODE) {
                    finish()
                } else {
                    return
                }
            }
        })

        setupEventsObserver()
    }

    private fun setupEventsObserver() {
        runEventsCollector(viewModel) { event ->
            when (val payload = event.getContent()) {
                is OpenSettings -> openSettingsScreen()
                is ShowToast -> showToast(payload.message)
            }
        }
    }

    private fun openEditModeScreen() {
        EditModeActivity.start(this, EditModeType.CREATE_MODE)
    }

    private fun openGalleryScreen() {
        GalleryActivity.start(this)
    }

    private fun openSettingsScreen() {
        SettingsActivity.start(this)
    }

    private fun showToast(message: String) {
        Toast.makeText(
            this,
            message,
            Toast.LENGTH_SHORT,
        ).show()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadData()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        viewModel.loadData()
    }
}

@Composable
fun NavigationMenuScreen(
    onGalleryButtonClicked: () -> Unit,
    onEditModeButtonClicked: () -> Unit,
    onSettingsButtonClicked: () -> Unit,
    appMode: AppMode,
    shouldShowAskForPasswordDialog: Boolean,
    changeDialogState: (Boolean) -> Unit,
    passwordValue: String,
    onPasswordValueChange: (String) -> Unit
) {

    NavigationMenuContent(
        onGalleryButtonClicked = onGalleryButtonClicked,
        onEditModeButtonClicked = onEditModeButtonClicked,
        onSettingsButtonClicked = onSettingsButtonClicked,
        appMode = appMode,
        shouldShowAskForPasswordDialog = shouldShowAskForPasswordDialog,
        changeDialogState = changeDialogState,
        passwordValue = passwordValue,
        onPasswordValueChange = onPasswordValueChange
    )
}

@Composable
fun NavigationMenuContent(
    onGalleryButtonClicked: () -> Unit,
    onEditModeButtonClicked: () -> Unit,
    onSettingsButtonClicked: () -> Unit,
    appMode: AppMode,
    shouldShowAskForPasswordDialog: Boolean,
    changeDialogState: (Boolean) -> Unit,
    passwordValue: String,
    onPasswordValueChange: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
    ) {
        if (shouldShowAskForPasswordDialog) {
            AskForPasswordDialog(
                changeDialogState = changeDialogState,
                passwordValue = passwordValue,
                onPasswordValueChange = onPasswordValueChange,
                focusManager = focusManager
            )
        }
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val (buttons) = createRefs()

            Column(
                modifier = Modifier
                    .constrainAs(buttons) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    }
            ) {
                MenuButton(
                    image = R.drawable.baseline_collections_24,
                    text = R.string.go_to_gallery_button,
                    onButtonClicked = onGalleryButtonClicked
                )
                Spacer(modifier = Modifier.height(16.dp))
                if (appMode == AppMode.PARENTAL_MODE) {
                    MenuButton(
                        image = R.drawable.baseline_edit_24,
                        text = R.string.go_to_edit_mode_button,
                        onButtonClicked = onEditModeButtonClicked
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                MenuButton(
                    image = R.drawable.settings_48px,
                    text = R.string.go_to_settings_button,
                    onButtonClicked = onSettingsButtonClicked
                )
            }
        }
    }
}

@Composable
private fun AskForPasswordDialog(
    changeDialogState: (Boolean) -> Unit,
    passwordValue: String,
    onPasswordValueChange: (String) -> Unit,
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
                        text = stringResource(R.string.write_password),
                        fontSize = 20.sp,
                        modifier = Modifier
                            .padding(bottom = dimensionResource(R.dimen.margin_large))
                    )
                    OutlinedTextField(
                        value = passwordValue,
                        onValueChange = { onPasswordValueChange(it) },
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
                        Text(text = stringResource(R.string.confirm_label))
                    }
                }
            }
        }
    }
}


@Preview(
    showBackground = true,
    widthDp = 1000,
    heightDp = 800
)
@Composable
fun NavigationActivityPreview(){
    NavigationMenuContent(
        onGalleryButtonClicked = { },
        onEditModeButtonClicked = {},
        onSettingsButtonClicked = {},
        appMode = AppMode.PARENTAL_MODE,
        shouldShowAskForPasswordDialog = false,
        changeDialogState = {},
        passwordValue = "123456",
        onPasswordValueChange = {}
    )
}
