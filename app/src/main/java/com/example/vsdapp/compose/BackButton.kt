package com.example.vsdapp.compose

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.vsdapp.R

@Composable
fun BackButton(
    text: String,
    onButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onButtonClicked,
        elevation = ButtonDefaults.elevation(0.dp, 0.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent,
            contentColor = colorResource(id = R.color.black)
        ),
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back arrow"
        )
        Text(
            text = text,
            modifier = Modifier
                .padding(start = 4.dp)
        )
    }
}

@Composable
@Preview
private fun BackButtonPreview(){
    BackButton(
        text = "Powrót",
        onButtonClicked = {  }
    )
}
