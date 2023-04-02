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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.vsdapp.R

@Composable
fun TopNavBar(
    onBackClicked: (() -> Unit)?,
    onRightClicked: () -> Unit,
    title: String? = null,
    onSaveButtonClicked: (() -> Unit)?,
    onTitleChanged: ((String) -> Unit)?,
    leftText: String,
    rightText: String,
    dropdownMenuContent: (@Composable () -> Unit) = {},
    searchFieldVisibility: Boolean,
    leftButtonVisibility: Boolean,
    rightButtonVisibility: Boolean
) {
    TopAppBar(
        elevation = 0.dp,
        backgroundColor = colorResource(id = R.color.nav_bar_background)
    ) {
        ConstraintLayout(
            modifier = Modifier.fillMaxWidth()
        ) {
            val (backButton, backText, titleInputField, saveButton, rightTextRef, rightButton) = createRefs()
            if (leftButtonVisibility && onBackClicked != null) {
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
                    text = leftText,
                    modifier = Modifier
                        .constrainAs(backText) {
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(backButton.end)
                        }
                        .clickable(enabled = true, onClick = onBackClicked)
                )
            }
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
                            end.linkTo(rightButton.start)
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
//                Text(
//                    text = rightText,
//                    modifier = Modifier
//                        .constrainAs(rightTextRef) {
//                            top.linkTo(parent.top)
//                            bottom.linkTo(parent.bottom)
//                            end.linkTo(rightButton.start)
//                        }
//                        .clickable(enabled = true, onClick = onRightClicked)
//                )
//                IconButton(
//                    onClick = onRightClicked,
//                    modifier = Modifier
//                        .constrainAs(rightButton) {
//                            top.linkTo(parent.top)
//                            bottom.linkTo(parent.bottom)
//                            end.linkTo(parent.end)
//                        }
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.ArrowForward,
//                        contentDescription = "Forward arrow"
//                    )
//                }
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
                    .width(125.dp)
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
private fun OverflowMenu(modifier: Modifier, content: @Composable () -> Unit) {
    var showMenu by remember { mutableStateOf(false) }

    Row(
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
            onDismissRequest = { showMenu = false }
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
        onRightClicked = {},
        title = "",
        onSaveButtonClicked = {},
        onTitleChanged = {},
        leftText = "Powrót",
        rightText = "Tryb odczytu",
        searchFieldVisibility = true,
        leftButtonVisibility = true,
        rightButtonVisibility = true
    )
}

