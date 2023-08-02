package com.example.vsdapp.studentsGallery

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil.compose.rememberImagePainter
import com.example.vsdapp.R
import com.example.vsdapp.compose.GalleryTopNavBar
import com.example.vsdapp.compose.LoadingScreen
import com.example.vsdapp.compose.NoResultsDisclaimer
import com.example.vsdapp.compose.SegmentedButtons
import com.example.vsdapp.core.Constants
import com.example.vsdapp.core.ViewState
import com.example.vsdapp.models.SceneDetails
import com.example.vsdapp.models.UserModel
import com.example.vsdapp.readMode.ReadModeActivity
import com.example.vsdapp.views.PictogramDetails
import org.koin.androidx.viewmodel.ext.android.viewModel


class StudentsGalleryActivity: AppCompatActivity() {

    companion object {
        fun start(activity: Activity, userModel: UserModel) {
            val intent = Intent(activity, StudentsGalleryActivity::class.java)
                .putExtra(Constants.INTENT_USER_MODEL, userModel)
            activity.startActivity(intent)
        }
    }

    private val viewModel by viewModel<StudentsGalleryViewModel>()
    private lateinit var userModel: UserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userModel = intent.getParcelableExtra<UserModel>(Constants.INTENT_USER_MODEL)!!

        viewModel.setInitialData(userModel)

        setContent { StudentsGalleryScreen() }
    }

    private fun onSceneClicked(scene: SceneDetails) {
        ReadModeActivity.start(this, scene.id, scene.imageLocation, userModel.userId)
    }

    @Composable
    fun StudentsGalleryScreen() {

        Crossfade(targetState = viewModel.viewStateFlow.collectAsState().value,
            label = ""
        ) { viewState ->
            when (viewState) {
                is ViewState.Progress -> LoadingScreen()
                is ViewState.Content -> {
                    StudentsGalleryContent(
                        onBackButtonClicked = { finish() },
                        searchText = viewModel.searchInput.value,
                        onSearchStringChanged = { viewModel.onSearchStringChanged(it) },
                        onSearchButtonClicked = { viewModel.onSearchButtonClicked() },
                        scenesList = viewModel.scenesList.value,
                        onSceneClicked = { onSceneClicked(it) },
                        shouldShowNoResultsDisclaimer = viewModel.shouldShowNoResultsDisclaimer.value,
                        onAddBookmarkSceneClicked = { viewModel.onAddBookmarkSceneClicked(it) },
                        userName = viewModel.userName.value,
                        userSurname = viewModel.userSurname.value,
                        onTabClicked = { viewModel.onTabClicked(it) },
                        tabIndex = viewModel.tabIndex.value
                    )
                }
                else -> {}
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StudentsGalleryContent(
    onBackButtonClicked: () -> Unit,
    searchText: String,
    onSearchStringChanged: (String) -> Unit,
    onSearchButtonClicked: () -> Unit,
    scenesList: List<SceneDetails>,
    onSceneClicked: (SceneDetails) -> Unit,
    shouldShowNoResultsDisclaimer: Boolean,
    onAddBookmarkSceneClicked: (SceneDetails) -> Unit,
    userName: String,
    userSurname: String,
    tabIndex: Int,
    onTabClicked: (Int) -> Unit
) {
    val focusManager = LocalFocusManager.current

    val onSearchButtonClickedAction = {
        onSearchButtonClicked()
        focusManager.clearFocus()
    }


    val tabs = listOf(stringResource(R.string.all_label), stringResource(R.string.marked_label))

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
            Text(
                text = stringResource(R.string.user_gallery, userName, userSurname),
                fontSize = 28.sp,
                modifier = Modifier
                    .padding(
                        top = dimensionResource(R.dimen.margin_large),
                        bottom = dimensionResource(R.dimen.margin_large)
                    )
            )
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
            
            SegmentedButtons(
                buttonsTitles = tabs,
                onButtonClicked = onTabClicked,
                selectedButton = tabIndex
            )
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_large)))

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
                                IconButton(
                                    onClick = { onAddBookmarkSceneClicked(scene) },
                                    modifier = Modifier
                                        .size(48.dp)
                                        .padding(end = 16.dp)
                                ) {
                                    Image(
                                        painter = if (scene.markedByTherapist) painterResource(R.drawable.bookmark_filled_48px) else painterResource(R.drawable.bookmark_48px),
                                        contentDescription = "Delete icon",
                                        colorFilter = ColorFilter.tint(colorResource(R.color.light_purple)),
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

@Preview(
    showBackground = true,
    widthDp = 800
)
@Composable
fun GalleryContentPreview() {
    StudentsGalleryContent(
        onBackButtonClicked = {  },
        searchText = "",
        onSearchStringChanged = { },
        onSearchButtonClicked = {  },
        scenesList = listOf(
            SceneDetails(id = "1", title = "obraz1", imageLocation = "url", pictograms = listOf(
                PictogramDetails(imageUrl = "url", x = 1, y = 1, label = "label", imageSize = 200, viewWidth = 216, viewHeight = 300)
            )),
            SceneDetails(id = "2", title = "obraz2", imageLocation = "url2", pictograms = listOf(
                PictogramDetails(imageUrl = "url", x = 1, y = 1, label = "label", imageSize = 200, viewWidth = 216, viewHeight = 300)
            ))
        ),
        onSceneClicked = {},
        shouldShowNoResultsDisclaimer = false,
        onAddBookmarkSceneClicked = {},
        userName = "Aneta",
        userSurname = "Klej",
        onTabClicked = {},
        tabIndex = 1
    )
}