package com.example.vsdapp.gallery

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.*
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
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
                   viewModel = viewModel,
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

}

@Composable
fun GalleryScreen(
    viewModel: GalleryViewModel,
    onBackButtonClicked: () -> Unit,
    onSceneClicked: (Scene) -> Unit,
    getImageFromInternalStorage: (String) -> Bitmap?,
) {
    val scenesList: List<Scene> by viewModel.scenesListFlow.collectAsState(listOf())

    GalleryContent(
        onBackButtonClicked = onBackButtonClicked,
        searchText = viewModel.searchInput.value,
        onSearchStringChanged = { viewModel.onSearchStringChanged(it) },
        onSearchButtonClicked = { viewModel.onSearchButtonClicked() },
        scenesList = scenesList,
        onSceneClicked = { onSceneClicked(it) },
        getImageFromInternalStorage = { getImageFromInternalStorage(it) },
        onDeleteSceneClicked = { viewModel.onDeleteSceneClicked(it) }
    )
}

@Composable
fun GalleryContent(
    onBackButtonClicked: () -> Unit,
    searchText: String,
    onSearchStringChanged: (String) -> Unit,
    onSearchButtonClicked: () -> Unit,
    scenesList: List<Scene>,
    onSceneClicked: (Scene) -> Unit,
    getImageFromInternalStorage: ((String) -> Bitmap?)? = null,
    onDeleteSceneClicked: (Scene) -> Unit
) {
    Column {
        GalleryTopNavBar(
            onBackClicked = onBackButtonClicked,
            backButtonText = stringResource(R.string.top_nav_bar_back_arrow_text)
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
        ) {
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
            if (scenesList.isNotEmpty()) {
                LazyColumn {
                    items(scenesList) { scene ->
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
                                            painter = rememberImagePainter(
                                                getImageFromInternalStorage(scene.imageLocation)
                                            ),
                                            contentDescription = null,
                                            modifier = Modifier.size(150.dp)
                                        )
                                    } else {
                                        Image(
                                            painter = rememberImagePainter(R.drawable.ic_launcher_foreground),
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
                                Image(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "Delete icon",
                                    colorFilter = ColorFilter.tint(colorResource(id = R.color.red_x)),
                                    modifier = Modifier
                                        .size(48.dp)
                                        .padding(end = 16.dp)
                                        .clickable { onDeleteSceneClicked.invoke(scene) }
                                )
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
            Scene(id = 1, imageName = "obraz1", imageLocation = "url", pictograms = listOf(PictogramDetails(imageUrl = "url", x = 1, y = 1, label = "label"))),
            Scene(id = 2, imageName = "obraz2", imageLocation = "url2", pictograms = listOf(PictogramDetails(imageUrl = "url", x = 1, y = 1, label = "label")))
        ),
        onSceneClicked = {},
        onDeleteSceneClicked = {}
    )
}