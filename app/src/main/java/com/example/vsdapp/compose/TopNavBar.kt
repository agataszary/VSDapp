package com.example.vsdapp.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.vsdapp.R

@Composable
fun TopNavBar(
    onBackClicked: (() -> Unit),
    title: String? = null,
    onSaveButtonClicked: (() -> Unit)?,
    onTitleChanged: ((String) -> Unit)?,
    leftText: String,
    dropdownMenuContent: (@Composable () -> Unit) = {},
    searchFieldVisibility: Boolean,
    rightButtonVisibility: Boolean
) {
    TopAppBar(
        elevation = 0.dp,
        backgroundColor = colorResource(id = R.color.nav_bar_background)
    ) {
        ConstraintLayout(
            modifier = Modifier.fillMaxWidth()
        ) {
            val (backButton, titleInputField, saveButton, rightButton) = createRefs()
            BackButton(
                text = leftText,
                onButtonClicked = onBackClicked,
                modifier = Modifier
                    .constrainAs(backButton) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    }
            )

            if (searchFieldVisibility && title != null && onSaveButtonClicked != null) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { onTitleChanged?.invoke(it) },
                    singleLine = true,
                    modifier = Modifier
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .width(300.dp)
                        .height(50.dp)
                        .constrainAs(titleInputField) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        }
                )
                Button(
                    onClick = onSaveButtonClicked,
                    enabled = title != "",
                    modifier = Modifier
                        .constrainAs(saveButton) {
                            start.linkTo(titleInputField.end)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        }
                        .padding(start = 8.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.save_button_title)
                    )
                }
            }
            if (rightButtonVisibility) {
                OverflowMenu(modifier = Modifier
                    .padding(end = dimensionResource(id = R.dimen.margin_small))
                    .constrainAs(rightButton) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)
                    },
                    content = dropdownMenuContent
                )
            } else {
                Spacer(modifier = Modifier
                    .width(80.dp)
                    .constrainAs(rightButton) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)
                    }
                )
            }
        }
    }
}

@Composable
fun OverflowMenu(modifier: Modifier, content: @Composable () -> Unit) {
    var showMenu by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
    ){
        IconButton(onClick = {
            showMenu = !showMenu
        }) {
            Icon(
                imageVector = Icons.Outlined.MoreVert,
                contentDescription = "More menu",
            )
        }
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false },
            properties = PopupProperties(dismissOnClickOutside = true)
        ) {
            content()
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 800
)
@Composable
fun TopNavBarPreview() {
    TopNavBar(
        onBackClicked = {},
        title = "",
        onSaveButtonClicked = {},
        onTitleChanged = {},
        leftText = "Powrót",
        searchFieldVisibility = true,
        rightButtonVisibility = false
    )
}

