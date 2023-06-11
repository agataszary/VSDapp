package com.example.vsdapp.settings

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.IconToggleButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.vsdapp.R
import com.example.vsdapp.compose.GalleryTopNavBar
import com.example.vsdapp.compose.LoadingScreen
import com.example.vsdapp.compose.SegmentedButtons
import com.example.vsdapp.compose.TopNavBar
import com.example.vsdapp.core.AppMode
import com.example.vsdapp.core.CloseActivity
import com.example.vsdapp.core.DeleteScene
import com.example.vsdapp.core.ViewState
import com.example.vsdapp.core.runEventsCollector
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
            }
        }
    }

    @Composable
    fun SettingsScreen() {
        when (viewModel.viewStateFlow.collectAsState().value) {
            is ViewState.Content -> SettingsContent(
                onBackClicked = { viewModel.onBackClicked() },
                selectedAppMode = viewModel.appModeState.value,
                onAppModeButtonClicked = { viewModel.onAppModeButtonClicked(it) }
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
    onAppModeButtonClicked: (Int) -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = { GalleryTopNavBar(
            onBackClicked = { onBackClicked() },
            backButtonText = stringResource(R.string.top_nav_bar_back_arrow_text)
        ) }
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(it)){
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
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
            }
        }
    }

}

@Composable
@Preview
private fun SettingsContentPreview() {
    SettingsContent(
        onBackClicked = {},
        onAppModeButtonClicked = {},
        selectedAppMode = AppMode.PARENTAL_MODE
    )
}