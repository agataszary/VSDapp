package com.example.vsdapp.navigationMenu

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.vsdapp.R
import com.example.vsdapp.compose.LoadingScreen
import com.example.vsdapp.compose.MenuButton
import com.example.vsdapp.core.AppMode
import com.example.vsdapp.core.Constants
import com.example.vsdapp.core.ViewState
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
                    onSettingsButtonClicked = { openSettingsScreen() },
                    appMode = viewModel.appMode.value
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

    override fun onResume() {
        super.onResume()
        viewModel.loadData()
    }
}

@Composable
fun NavigationMenuScreen(
    onGalleryButtonClicked: () -> Unit,
    onEditModeButtonClicked: () -> Unit,
    onSettingsButtonClicked: () -> Unit,
    appMode: AppMode
) {

    NavigationMenuContent(
        onGalleryButtonClicked = onGalleryButtonClicked,
        onEditModeButtonClicked = onEditModeButtonClicked,
        onSettingsButtonClicked = onSettingsButtonClicked,
        appMode = appMode
    )
}

@Composable
fun NavigationMenuContent(
    onGalleryButtonClicked: () -> Unit,
    onEditModeButtonClicked: () -> Unit,
    onSettingsButtonClicked: () -> Unit,
    appMode: AppMode
) {
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
            if (appMode == AppMode.PARENTAL_MODE){
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
        appMode = AppMode.PARENTAL_MODE
    )
}
