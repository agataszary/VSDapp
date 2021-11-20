package com.example.vsdapp.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.vsdapp.R

@Composable
fun GalleryTopNavBar(
    onBackClicked: () -> Unit
) {
    TopAppBar(
        elevation = 0.dp,
        backgroundColor = colorResource(id = R.color.nav_bar_background)
    ) {
        ConstraintLayout(
            modifier = Modifier.fillMaxWidth()
        ) {
            val (backButton, backText) = createRefs()
            IconButton(
                onClick = onBackClicked,
                modifier = Modifier
                    .constrainAs(backButton) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back arrow"
                )
            }
            Text(
                text = stringResource(id = R.string.top_nav_bar_back_arrow_text),
                modifier = Modifier
                    .constrainAs(backText) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(backButton.end)
                    }
                    .clickable(enabled = true, onClick = onBackClicked)
            )
        }
    }
}

@Preview
@Composable
fun GalleryTopNavBarPreview() {
    GalleryTopNavBar(onBackClicked = {})
}