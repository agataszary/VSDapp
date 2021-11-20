package com.example.vsdapp.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.vsdapp.R

@Composable
fun MenuButton(
    image: Int,
    text: Int,
    onButtonClicked: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(40.dp),
        border = BorderStroke(
            width = 2.dp,
            color = colorResource(id = R.color.nav_bar_background)
        ),
        elevation = 0.dp,
        modifier = Modifier
            .height(70.dp)
            .width(300.dp)
            .clickable { onButtonClicked() }
    ) {
        ConstraintLayout {
            val (imageRef, textRef) = createRefs()

            Image(
                painter = painterResource(id = image),
                contentDescription = "Gallery",
                colorFilter = ColorFilter.tint(colorResource(id = R.color.light_purple)),
                modifier = Modifier
                    .size(48.dp)
                    .padding(
                        start = 16.dp
                    )
                    .constrainAs(imageRef) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    }
            )
            Text(
                text = stringResource(id = text),
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                modifier = Modifier
                    .constrainAs(textRef) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )
        }
    }
}

@Preview(
    showBackground = true
)
@Composable
fun MenuButtonPreview() {
    MenuButton(
        image = R.drawable.baseline_collections_24,
        text = R.string.go_to_gallery_button,
        onButtonClicked = {}
    )
}