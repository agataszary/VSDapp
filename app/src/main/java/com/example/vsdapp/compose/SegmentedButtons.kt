package com.example.vsdapp.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.vsdapp.R

@Composable
fun SegmentedButtons(
    firstButtonContent: String,
    secondButtonContent: String,
    onButtonClicked: (Int) -> Unit,
    selectedButton: Int
) {
    Row {
        OutlinedButton(
            onClick = { onButtonClicked(0) },
            shape = RoundedCornerShape(
                topStartPercent = 48,
                topEndPercent = 0,
                bottomStartPercent = 48,
                bottomEndPercent = 0
            ),
            colors = ButtonDefaults.outlinedButtonColors(
                backgroundColor = if (selectedButton == 0) colorResource(R.color.nav_bar_background) else colorResource(R.color.white),
                contentColor = colorResource(R.color.dark_purple_gray)
            ),
            border = BorderStroke(
                width = 1.dp,
                color = colorResource(R.color.dark_purple_gray)
            ),
            modifier = Modifier
                .width(200.dp)
        ) {
            if (selectedButton == 0){
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "",
                    modifier = Modifier
                        .padding(end = dimensionResource(R.dimen.margin_tiny))
                        .size(dimensionResource(R.dimen.small_icon_size))
                )
            }
            Text(
                text = firstButtonContent
            )
        }
        OutlinedButton(
            onClick = { onButtonClicked(1) },
            shape = RoundedCornerShape(
                topStartPercent = 0,
                topEndPercent = 48,
                bottomStartPercent = 0,
                bottomEndPercent = 48
            ),
            colors = ButtonDefaults.outlinedButtonColors(
                backgroundColor = if (selectedButton == 1) colorResource(R.color.nav_bar_background) else colorResource(R.color.white),
                contentColor = colorResource(R.color.dark_purple_gray)
            ),
            border = BorderStroke(
                width = 1.dp,
                color = colorResource(R.color.dark_purple_gray)
            ),
            modifier = Modifier
                .width(200.dp)
        ) {
            if (selectedButton == 1){
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "",
                    modifier = Modifier
                        .padding(end = dimensionResource(R.dimen.margin_tiny))
                        .size(dimensionResource(R.dimen.small_icon_size))
                )
            }
            Text(
                text = secondButtonContent
            )
        }
    }
}

@Composable
@Preview
private fun SegmentedButtonsPreview() {
    SegmentedButtons(
        firstButtonContent = "Tryb rodzica",
        secondButtonContent = "Tryb dziecka",
        onButtonClicked = {},
        selectedButton = 1
    )
}