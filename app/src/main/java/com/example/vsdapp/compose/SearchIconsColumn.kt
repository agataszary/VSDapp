package com.example.vsdapp.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.vsdapp.R
import com.example.vsdapp.editMode.EditModeViewModel
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun SearchIconsColumn(
    textFieldValue: String,
    onSearchButtonClicked: () -> Unit,
    iconsList: List<String>,
    iconClicked: String,
    searchButtonEnabled: Boolean,
    onChangeBackgroundPictureClicked: () -> Unit,
    onSearchStringChanged: (String) -> Unit,
    onIconClicked: (String) -> Unit,
    choosePictureButtonVisibility: Boolean,
    shouldShowNoResultsDisclaimer: Boolean
) {
    val focusManager = LocalFocusManager.current

    val onSearchButtonClickedAction = {
        onSearchButtonClicked()
        focusManager.clearFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        if (choosePictureButtonVisibility) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
            ) {

                Button(
                    onClick = onChangeBackgroundPictureClicked,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.change_background_picture)
                    )
                }
            }
        }
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = textFieldValue,
            onValueChange = { onSearchStringChanged.invoke(it) },
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = { onSearchStringChanged("") }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close icon",
                        tint = colorResource(R.color.gray)
                    )
                }
            }
        )
        Button(
            onClick = onSearchButtonClickedAction,
            enabled = searchButtonEnabled,
            modifier = Modifier.padding(top = 8.dp, bottom = 12.dp),
        ) {
            Text(
                text = stringResource(id = R.string.search_button_title)
            )
        }
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                items(iconsList) { icon ->
                    Card(
                        modifier = if (icon == iconClicked) Modifier.border(4.dp, MaterialTheme.colors.primary.copy(alpha = ContentAlpha.high)) else Modifier.border(1.dp, Color.Black)
                    ) {
                        Image(
                            painter = rememberImagePainter(icon),
                            contentDescription = null,
                            modifier = Modifier
                                .size(150.dp)
                                .clickable { onIconClicked.invoke(icon) }
                        )
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                }
            }
        }
    }
}

@Preview(
    showBackground = true
)
@Composable
fun SearchIconsColumnPreview() {
    SearchIconsColumn(
        textFieldValue = "",
        onSearchButtonClicked = {},
        iconsList = listOf("https://api.arasaac.org/api/pictograms/11463", "https://api.arasaac.org/api/pictograms/11461",
        "https://api.arasaac.org/api/pictograms/2625", "https://api.arasaac.org/api/pictograms/3248",
        "https://api.arasaac.org/api/pictograms/11403"),
        iconClicked = "",
        onChangeBackgroundPictureClicked = {},
        searchButtonEnabled = true,
        onSearchStringChanged = {},
        onIconClicked = {},
        choosePictureButtonVisibility = true,
        shouldShowNoResultsDisclaimer = true
    )
}