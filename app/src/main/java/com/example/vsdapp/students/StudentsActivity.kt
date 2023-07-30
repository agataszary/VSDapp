package com.example.vsdapp.students

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vsdapp.R
import com.example.vsdapp.compose.GalleryTopNavBar
import com.example.vsdapp.compose.LoadingScreen
import com.example.vsdapp.compose.OverflowMenu
import com.example.vsdapp.core.AppMode
import com.example.vsdapp.core.ViewState
import com.example.vsdapp.models.UserModel
import com.example.vsdapp.studentsGallery.StudentsGalleryActivity
import com.google.firebase.firestore.auth.User
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Vector

class StudentsActivity: AppCompatActivity() {

    companion object {
        fun start(activity: Activity) {
            val intent = Intent(activity, StudentsActivity::class.java)
            activity.startActivity(intent)
        }
    }

    private val viewModel by viewModel<StudentsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.loadData()

        setContent { StudentsScreen() }
    }

    private fun openUserGallery(userModel: UserModel) {
        StudentsGalleryActivity.start(this, userModel)
    }

    @Composable
    private fun StudentsScreen() {
        Crossfade(targetState = viewModel.viewStateFlow.collectAsState().value,
            label = ""
        ) { viewState ->
            when (viewState) {
                is ViewState.Progress -> LoadingScreen()
                is ViewState.Content -> {
                    StudentsContent(
                        tabIndex = viewModel.tabIndex.value,
                        onBackClicked = { finish() },
                        onTabClicked = { viewModel.onTabClicked(it) },
                        yourStudentsList = viewModel.yourStudentsList.value,
                        availableUsersList = viewModel.searchedUsers.value,
                        onSearchButtonClicked = { viewModel.onSearchButtonClicked() },
                        onSearchStringChanged = { viewModel.onSearchStringChanged(it) },
                        searchText = viewModel.searchTextValue.value,
                        onAddIconClicked = { viewModel.onAddIconClicked(it) },
                        onOpenGalleryClicked = { openUserGallery(it) },
                        onDeleteUserClicked = { viewModel.onDeleteUserClicked(it) }
                    )
                }
                else -> {}
            }
        }
    }
}

@Composable
fun StudentsContent(
    tabIndex: Int,
    onBackClicked: () -> Unit,
    onTabClicked: (Int) -> Unit,
    yourStudentsList: List<UserModel>,
    availableUsersList: List<UserModel>,
    onSearchButtonClicked: () -> Unit,
    searchText: String,
    onSearchStringChanged: (String) -> Unit,
    onAddIconClicked: (String) -> Unit,
    onOpenGalleryClicked: (UserModel) -> Unit,
    onDeleteUserClicked: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current

    val onSearchButtonClickedAction = {
        onSearchButtonClicked()
        focusManager.clearFocus()
    }

    val tabs = listOf(stringResource(R.string.pupils_label), stringResource(R.string.search_button_title))

    Scaffold(
        topBar = { GalleryTopNavBar(
            onBackClicked = { onBackClicked() },
            backButtonText = stringResource(R.string.top_nav_bar_back_arrow_text)
        ) },
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
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = setScreenTitle(tabIndex = tabIndex),
                        fontSize = 26.sp,
                        modifier = Modifier
                            .padding(
                                top = dimensionResource(R.dimen.margin_xlarge),
                                bottom = dimensionResource(R.dimen.margin_xlarge)
                            )
                    )
                }

                TabRow(
                    selectedTabIndex = tabIndex,
                    backgroundColor = colorResource(R.color.light_gray)
                ) {
                    tabs.forEachIndexed { index, title->
                        Tab(
                            selected = tabIndex == index,
                            onClick = { onTabClicked(index) },
                            text = {
                                Text(text = title)
                            }
                        )

                    }
                }

                Crossfade(targetState = tabIndex, label = ""
                ) { tIndex ->
                    when (tIndex) {
                        0 -> StudentsView(
                            studentsList = yourStudentsList,
                            onDeleteUserClicked = onDeleteUserClicked,
                            onOpenGalleryClicked = onOpenGalleryClicked
                        )

                        1 -> SearchView(
                            usersList = availableUsersList,
                            onSearchButtonClickedAction = onSearchButtonClickedAction,
                            searchText = searchText,
                            onSearchStringChanged = onSearchStringChanged,
                            onAddIconClicked = onAddIconClicked
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun setScreenTitle(tabIndex: Int): String {
    return when(tabIndex) {
        0 -> stringResource(R.string.your_pupils_screen_title)
        1 -> stringResource(R.string.search_for_students)
        else -> ""
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StudentsView(
    studentsList: List<UserModel>,
    onOpenGalleryClicked: (UserModel) -> Unit,
    onDeleteUserClicked: (String) -> Unit ,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn {
            itemsIndexed(
                items = studentsList,
                key = { _, it -> it.userId}
            ){index, user ->
                if (index == 0) Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_large)))
                UserRow(
                    user = user,
                    isSearchFragment = false,
                    onOpenGalleryClicked = onOpenGalleryClicked,
                    onDeleteUserClicked = onDeleteUserClicked,
                    modifier = Modifier
                        .animateItemPlacement()
                )
            }
        }
    }
}

@Composable
private fun UserRow(
    user: UserModel,
    isSearchFragment: Boolean,
    onIconClicked: (String) -> Unit = {},
    onOpenGalleryClicked: (UserModel) -> Unit = {},
    onDeleteUserClicked: (String) -> Unit = {},
    modifier: Modifier
) {
    Card(
        border = BorderStroke(
            width = 2.dp,
            color = colorResource(id = R.color.nav_bar_background)
        ),
        elevation = 0.dp,
        modifier = modifier
            .width(400.dp)
            .padding(bottom = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(end = dimensionResource(R.dimen.margin_small))
        ) {
            Column(
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.margin_small))
                    .weight(1f)
            ) {
                val name = if (user.childName.isNullOrEmpty()) user.mainName else user.childName
                val surname = if (user.childSurname.isNullOrEmpty()) user.mainSurname else user.childSurname
                Text(text = "$name $surname")
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_tiny)))
                Text(text = user.emailAddress)
            }

            if (isSearchFragment) {
                IconButton(
                    onClick = { onIconClicked(user.userId) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "More menu"
                    )
                }
            } else {
                OverflowMenu(modifier = Modifier) {
                    DropdownMenuItem(onClick = { onOpenGalleryClicked(user) }) {
                        Text(text = stringResource(R.string.see_users_gallery))
                    }
                    DropdownMenuItem(onClick = { onDeleteUserClicked(user.userId) }) {
                        Text(text = stringResource(R.string.delete_user))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchView(
    usersList: List<UserModel>,
    searchText: String,
    onSearchStringChanged: (String) -> Unit,
    onSearchButtonClickedAction: () -> Unit,
    onAddIconClicked: (String) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(
                    top = 16.dp,
                    bottom = 8.dp,
                )
        ){
            OutlinedTextField(
                value = searchText,
                onValueChange = { onSearchStringChanged(it) },
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
                    .width(350.dp)
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
        LazyColumn {
            itemsIndexed(
                items = usersList,
                key = { _, it -> it.userId}
            ){index, user ->
                if (index == 0) Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_large)))
                UserRow(
                    user = user,
                    isSearchFragment = true,
                    onIconClicked = onAddIconClicked,
                    modifier = Modifier
                        .animateItemPlacement()
                )
            }
        }
    }
}

@Preview(
    widthDp = 600
)
@Composable
private fun StudentsContentPreview() {
    StudentsContent(
        tabIndex = 0,
        onBackClicked = {},
        onTabClicked = {},
        yourStudentsList = listOf(
            UserModel(mainName = "Aneta", mainSurname = "Klej", emailAddress = "aneta@mail.com", userId = "1"),
            UserModel(mainName = "Aneta", mainSurname = "Klej", emailAddress = "aneta@mail.com", childName = "Anetka", childSurname = "Klej", userId = "2")
        ),
        availableUsersList = listOf(
            UserModel(mainName = "Aneta", mainSurname = "Klej", emailAddress = "aneta@mail.com", userId = "1"),
            UserModel(mainName = "Aneta", mainSurname = "Klej", emailAddress = "aneta@mail.com", childName = "Anetka", childSurname = "Klej", userId = "2")
        ),
        onSearchStringChanged = {},
        onSearchButtonClicked = {},
        searchText = "Aneta",
        onAddIconClicked = {},
        onDeleteUserClicked = {},
        onOpenGalleryClicked = {}
    )
}