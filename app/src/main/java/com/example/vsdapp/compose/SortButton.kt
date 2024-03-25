package com.example.vsdapp.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.vsdapp.R
import com.example.vsdapp.gallery.SortByCategory

@Composable
fun SortButton(
    onSortByClicked: (SortByCategory) -> Unit,
    sortCategory: SortByCategory,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
//            .wrapContentSize(Alignment.TopStart)
    ) {
        TextButton(
            onClick = { expanded = true },
            colors = ButtonDefaults.textButtonColors(
                contentColor = colorResource(R.color.dark_purple_gray)
            )
        ) {
            Text(
                text = stringResource(R.string.sort_by_label)
            )
            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.margin_large)))
            Icon(
                painter = painterResource(R.drawable.sort_48px),
                contentDescription = "sort button",
                modifier = Modifier
                    .size(28.dp)
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(onClick = {
                onSortByClicked(SortByCategory.CREATION_DATE_DESC)
                expanded = false
            }) {
                Text(
                    text = stringResource(R.string.sort_by_creation_date_desc),
                    fontWeight = if (sortCategory == SortByCategory.CREATION_DATE_DESC) FontWeight.Bold else FontWeight.Normal
                )
            }
            DropdownMenuItem(onClick = {
                onSortByClicked(SortByCategory.CREATION_DATE_ASC)
                expanded = false
            }) {
                Text(
                    text = stringResource(R.string.sort_by_creation_date_asc),
                    fontWeight = if (sortCategory == SortByCategory.CREATION_DATE_ASC) FontWeight.Bold else FontWeight.Normal
                )
            }
            DropdownMenuItem(onClick = {
                onSortByClicked(SortByCategory.UPDATE_DATE)
                expanded = false
            }) {
                Text(
                    text = stringResource(R.string.sort_by_update_date),
                    fontWeight = if (sortCategory == SortByCategory.UPDATE_DATE) FontWeight.Bold else FontWeight.Normal
                )
            }
            DropdownMenuItem(onClick = {
                onSortByClicked(SortByCategory.ALPHABETICAL)
                expanded = false
            }) {
                Text(
                    text = stringResource(R.string.sort_by_alphabetical),
                    fontWeight = if (sortCategory == SortByCategory.ALPHABETICAL) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Preview(
    showBackground = true
)
@Composable
private fun SortButtonPreview() {
    SortButton(
        onSortByClicked = {},
        sortCategory = SortByCategory.UPDATE_DATE
    )
}