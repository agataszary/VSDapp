package com.example.vsdapp.navigationMenu

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.vsdapp.R
import com.example.vsdapp.compose.MenuButton
import com.example.vsdapp.editMode.EditModeActivity
import com.example.vsdapp.editMode.EditModeType
import com.example.vsdapp.gallery.GalleryActivity

class NavigationActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NavigationMenuScreen(
                onGalleryButtonClicked = { openGalleryScreen() },
                onEditModeButtonClicked = { openEditModeScreen() }
            )
        }
    }

    private fun openEditModeScreen() {
        EditModeActivity.start(this, EditModeType.CREATE_MODE)
    }

    private fun openGalleryScreen() {
        GalleryActivity.start(this)
    }
}

@Composable
fun NavigationMenuScreen(
    onGalleryButtonClicked: () -> Unit,
    onEditModeButtonClicked: () -> Unit
) {

    NavigationMenuContent(
        onGalleryButtonClicked = onGalleryButtonClicked,
        onEditModeButtonClicked = onEditModeButtonClicked
    )
}

@Composable
fun NavigationMenuContent(
    onGalleryButtonClicked: () -> Unit,
    onEditModeButtonClicked: () -> Unit
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
            MenuButton(
                image = R.drawable.baseline_edit_24,
                text = R.string.go_to_edit_mode_button,
                onButtonClicked = onEditModeButtonClicked
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
        onEditModeButtonClicked = {}
    )
}
