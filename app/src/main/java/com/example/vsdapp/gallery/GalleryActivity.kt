package com.example.vsdapp.gallery

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.*
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
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

class GalleryActivity: AppCompatActivity(){

    lateinit var viewModel: GalleryViewModel
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

        viewModel = GalleryViewModel()
        viewModel.setInitialData(db.sceneDao)

        setContent {
            when (viewModel.viewStateFlow.collectAsState().value) {
               is ViewState.Content ->  GalleryScreen(
                   onBackButtonClicked = { finish() },
                   onSceneClicked = { onSceneClicked(it) },
                   getImageFromInternalStorage =  {name ->
                       loadPhotoFromInternalStorage(name)
                   }
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

    private fun onSceneClicked(scene: Scene) {
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

    private  fun loadPhotoFromInternalStorage(filename: String): Bitmap? {
        val files = filesDir.listFiles { file ->
            file.canRead() && file.isFile && file.name == filename
        }
        return if (files != null && files.size == 1) {
            val bytes = files[0].readBytes()
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } else {
            null
        }
    }

    @Composable
    fun GalleryScreen(
        onBackButtonClicked: () -> Unit,
        onSceneClicked: (Scene) -> Unit,
        getImageFromInternalStorage: (String) -> Bitmap?,
    ) {
//    val scenesList: List<Scene> by viewModel.scenesListFlow.collectAsState(listOf())

        GalleryContent(
            onBackButtonClicked = onBackButtonClicked,
            searchText = viewModel.searchInput.value,
            onSearchStringChanged = { viewModel.onSearchStringChanged(it) },
            onSearchButtonClicked = { viewModel.onSearchButtonClicked() },
            scenesList = viewModel.scenesList.value,
            onSceneClicked = { onSceneClicked(it) },
            getImageFromInternalStorage = { getImageFromInternalStorage(it) },
            onDeleteSceneClicked = { viewModel.onDeleteSceneClicked(it) },
            changeAlertDialogState = { viewModel.changeAlertDialogState(it) },
            openAlertDialog = viewModel.openAlertDialog.value,
            onConfirmDeleteClicked = { viewModel.onConfirmDeleteClicked() }
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
    scenesList: List<Scene>,
    onSceneClicked: (Scene) -> Unit,
    getImageFromInternalStorage: ((String) -> Bitmap?)? = null,
    openAlertDialog: Boolean,
    onDeleteSceneClicked: (Scene) -> Unit,
    changeAlertDialogState: (Boolean) -> Unit,
    onConfirmDeleteClicked: () -> Unit
) {
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
                    modifier = Modifier
                        .width(400.dp)
                )
                Button(
                    onClick = onSearchButtonClicked,
                    enabled = searchText != "",
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.search_button_title)
                    )
                }
            }
//            if (scenesList.isNotEmpty()) {
                LazyColumn {
                    items(
                        items = scenesList,
                        key = { it.id }
                    ) { scene ->
                        val image = remember { getImageFromInternalStorage?.let { it(scene.imageLocation) } }
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
                                    if (getImageFromInternalStorage != null) {
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
                                    text = scene.imageName,
                                    modifier = Modifier
                                        .padding(start = 16.dp)
                                        .weight(1f)
                                )
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
//            }
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
            Scene(id = 1, imageName = "obraz1", imageLocation = "url", pictograms = listOf(PictogramDetails(imageUrl = "url", x = 1, y = 1, label = "label", imageSize = 200, viewWidth = 216, viewHeight = 300))),
            Scene(id = 2, imageName = "obraz2", imageLocation = "url2", pictograms = listOf(PictogramDetails(imageUrl = "url", x = 1, y = 1, label = "label", imageSize = 200, viewWidth = 216, viewHeight = 300)))
        ),
        onSceneClicked = {},
        onDeleteSceneClicked = {},
        changeAlertDialogState = {},
        openAlertDialog = false,
        onConfirmDeleteClicked = {}
    )
}