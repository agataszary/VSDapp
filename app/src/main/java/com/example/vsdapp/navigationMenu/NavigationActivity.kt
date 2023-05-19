package com.example.vsdapp.navigationMenu

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.vsdapp.R
import com.example.vsdapp.compose.MenuButton
import com.example.vsdapp.editMode.EditModeActivity
import com.example.vsdapp.editMode.EditModeType
import com.example.vsdapp.gallery.GalleryActivity
import kotlin.math.roundToInt

class NavigationActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
//            NavigationMenuScreen(
//                onGalleryButtonClicked = { openGalleryScreen() },
//                onEditModeButtonClicked = { openEditModeScreen() }
//            )
            Test()
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
fun Test (){
    Column(
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        // set up all transformation states
        var scale by remember { mutableStateOf(1f) }
        var rotation by remember { mutableStateOf(0f) }
        var offset by remember { mutableStateOf(Offset.Zero) }
        var offsetX by remember { mutableStateOf(0f) }
        var offsetY by remember { mutableStateOf(0f) }
        val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
            scale *= zoomChange
            rotation += rotationChange
            offsetX += offsetChange.x
            offsetY += offsetChange.y
        }

        Box(
            Modifier
                // apply other transformations like rotation and zoom
                // on the pizza slice emoji
                .absoluteOffset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
//                    rotationZ = rotation,
                    translationX = offset.x,
                    translationY = offset.y
                )
                // add transformable to listen to multitouch transformation events
                // after offset
                .pointerInput(Unit) {
                    detectDragGesturesAfterLongPress { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                        println("x: $offsetX, y: $offsetY")
                    }
                }
                .transformable(state = state)
                .background(Color.Blue)
                .size(300.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Face,
                contentDescription = "",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            )
        }
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
