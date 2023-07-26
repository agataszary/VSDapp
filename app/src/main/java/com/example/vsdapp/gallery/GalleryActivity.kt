package com.example.vsdapp.gallery

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.*
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.vsdapp.R
import com.example.vsdapp.compose.GalleryTopNavBar
import com.example.vsdapp.compose.LoadingScreen
import com.example.vsdapp.core.DeleteScene
import com.example.vsdapp.core.ViewState
import com.example.vsdapp.core.runEventsCollector
import com.example.vsdapp.database.AppDatabase
import com.example.vsdapp.database.Scene
import com.example.vsdapp.readMode.ReadModeActivity
import com.example.vsdapp.views.PictogramDetails
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.core.net.toUri
import com.example.vsdapp.compose.NoResultsDisclaimer
import com.example.vsdapp.core.AppMode
import com.example.vsdapp.models.SceneDetails
import org.koin.androidx.viewmodel.ext.android.viewModel

class GalleryActivity: AppCompatActivity(){

    private val viewModel by viewModel<GalleryViewModel>()
    lateinit var db: AppDatabase

    companion object {
        fun start(activity: Activity){
            val intent = Intent(activity, GalleryActivity::class.java)
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = AppDatabase.getInstance(this)

        viewModel.setInitialData(db.sceneDao)

        setContent {
            when (viewModel.viewStateFlow.collectAsState().value) {
               is ViewState.Content ->  GalleryScreen(
                   onBackButtonClicked = { finish() },
                   onSceneClicked = { onSceneClicked(it) }
               )
                is ViewState.Progress -> LoadingScreen()
                else -> {}
            }
        }

        setupEventsObserver()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadData()
    }

    private fun setupEventsObserver() {
        runEventsCollector(viewModel) { event ->
            when (val payload = event.getContent()) {
                is DeleteScene -> onDeleteSceneClicked(payload.scene)
            }
        }
    }

    private fun onSceneClicked(scene: SceneDetails) {
        ReadModeActivity.start(this, scene.id, scene.imageLocation)
    }

    private fun onDeleteSceneClicked(scene: Scene) {
        val files = filesDir.listFiles { file ->
            file.isFile && file.name == scene.imageLocation
        }
        if (files != null && files.isNotEmpty()) {
            deleteFile(scene.imageLocation)
        }
    }

    @Composable
    fun GalleryScreen(
        onBackButtonClicked: () -> Unit,
        onSceneClicked: (SceneDetails) -> Unit,
    ) {
//    val scenesList: List<Scene> by viewModel.scenesListFlow.collectAsState(listOf())

        GalleryContent(
            onBackButtonClicked = onBackButtonClicked,
            searchText = viewModel.searchInput.value,
            onSearchStringChanged = { viewModel.onSearchStringChanged(it) },
            onSearchButtonClicked = { viewModel.onSearchButtonClicked() },
            scenesList = viewModel.scenesList.value,
            onSceneClicked = { onSceneClicked(it) },
            onDeleteSceneClicked = { viewModel.onDeleteSceneClicked(it) },
            changeAlertDialogState = { viewModel.changeAlertDialogState(it) },
            openAlertDialog = viewModel.openAlertDialog.value,
            onConfirmDeleteClicked = { viewModel.onConfirmDeleteClicked() },
            appMode = viewModel.appMode.value,
            shouldShowNoResultsDisclaimer = viewModel.shouldShowNoResultsDisclaimer.value
        )
    }
}



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GalleryContent(
    onBackButtonClicked: () -> Unit,
    searchText: String,
    onSearchStringChanged: (String) -> Unit,
    onSearchButtonClicked: () -> Unit,
    scenesList: List<SceneDetails>,
    onSceneClicked: (SceneDetails) -> Unit,
    openAlertDialog: Boolean,
    onDeleteSceneClicked: (SceneDetails) -> Unit,
    changeAlertDialogState: (Boolean) -> Unit,
    onConfirmDeleteClicked: () -> Unit,
    appMode: AppMode,
    shouldShowNoResultsDisclaimer: Boolean
) {
    val focusManager = LocalFocusManager.current

    val onSearchButtonClickedAction = {
        onSearchButtonClicked()
        focusManager.clearFocus()
    }

    Column {
        GalleryTopNavBar(
            onBackClicked = { onBackButtonClicked() },
            backButtonText = stringResource(R.string.top_nav_bar_back_arrow_text)
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            if (openAlertDialog) {
                AlertDialog(
                    onDismissRequest = { changeAlertDialogState(false) },
                    title = {
                        Text(
                            text = stringResource(R.string.delete_scene_alert_title),
                            fontSize = 20.sp
                        )
                    },
                    text = {
                        Text(
                            text = stringResource(R.string.delete_scene_alert_body)
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = { onConfirmDeleteClicked() }
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

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(
                        top = 8.dp,
                        bottom = 16.dp,
                        start = 32.dp
                    )
            ){
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { onSearchStringChanged.invoke(it) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = { onSearchButtonClickedAction() }
                    ),
                    trailingIcon = {
                        IconButton(onClick = { onSearchStringChanged("") }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close icon",
                                tint = colorResource(R.color.gray)
                            )
                        }
                    },
                    modifier = Modifier
                        .width(400.dp)
                )
                Button(
                    onClick = onSearchButtonClickedAction,
                    enabled = searchText != "",
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.search_button_title)
                    )
                }
            }
            if (shouldShowNoResultsDisclaimer) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    NoResultsDisclaimer(
                        modifier = Modifier
                            .padding(top = 100.dp)
                            .align(Alignment.TopCenter)
                    )
                }
            } else {
                LazyColumn(
                    state = rememberLazyListState()
                ) {
                    items(
                        items = scenesList,
                        key = { it.id }
                    ) { scene ->
                        val image = remember { scene.imageUrl.toUri() }
                        Card(
                            border = BorderStroke(
                                width = 2.dp,
                                color = colorResource(id = R.color.nav_bar_background)
                            ),
                            elevation = 0.dp,
                            modifier = Modifier
                                .width(600.dp)
                                .clickable { onSceneClicked(scene) }
                                .padding(bottom = 4.dp)
                                .animateItemPlacement()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .padding(4.dp)
                            ) {
                                Card(
                                modifier = Modifier
                                    .border(1.dp, Color.Black)
                                ) {
                                    if (image != Uri.EMPTY) {
                                        Image(
                                            painter = rememberImagePainter(image),
                                            contentDescription = null,
                                            modifier = Modifier.size(150.dp)
                                        )
                                    } else {
                                        Image(
                                            painter = rememberImagePainter(
                                               remember { R.drawable.ic_launcher_foreground}
                                            ),
                                            contentDescription = null,
                                            modifier = Modifier.size(150.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = scene.title,
                                    modifier = Modifier
                                        .padding(start = 16.dp)
                                        .weight(1f)
                                )
                                if (appMode == AppMode.PARENTAL_MODE) {
                                    IconButton(
                                        onClick = { onDeleteSceneClicked(scene) },
                                        modifier = Modifier
                                            .size(48.dp)
                                            .padding(end = 16.dp)
                                    ) {
                                        Image(
                                            imageVector = Icons.Filled.Delete,
                                            contentDescription = "Delete icon",
                                            colorFilter = ColorFilter.tint(colorResource(id = R.color.red_x)),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 800
)
@Composable
fun GalleryContentPreview() {
    GalleryContent(
        onBackButtonClicked = {  },
        searchText = "",
        onSearchStringChanged = { },
        onSearchButtonClicked = {  },
        scenesList = listOf(
            SceneDetails(id = "1", title = "obraz1", imageLocation = "url", pictograms = listOf(PictogramDetails(imageUrl = "url", x = 1, y = 1, label = "label", imageSize = 200, viewWidth = 216, viewHeight = 300))),
            SceneDetails(id = "2", title = "obraz2", imageLocation = "url2", pictograms = listOf(PictogramDetails(imageUrl = "url", x = 1, y = 1, label = "label", imageSize = 200, viewWidth = 216, viewHeight = 300)))
        ),
        onSceneClicked = {},
        onDeleteSceneClicked = {},
        changeAlertDialogState = {},
        openAlertDialog = false,
        onConfirmDeleteClicked = {},
        appMode = AppMode.PARENTAL_MODE,
        shouldShowNoResultsDisclaimer = false
    )
}