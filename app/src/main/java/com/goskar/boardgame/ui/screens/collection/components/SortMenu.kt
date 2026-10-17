package com.goskar.boardgame.ui.screens.collection.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.theme.*
import com.goskar.boardgame.ui.theme.BoardGameTheme
import com.goskar.boardgame.utils.SortList


@Composable
fun SortMenu(
    modifier: Modifier = Modifier,
    current: SortList,
    onSort: (SortList) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.List,
                contentDescription = stringResource(R.string.cd_sort),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            SortList.entries.forEach { sort ->
                DropdownMenuItem(
                    text = {
                        Text(
                            stringResource(sort.value),
                            fontWeight = if (sort == current) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (sort == current) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    },
                    onClick = {
                        onSort(sort)
                        expanded = false
                    }
                )
            }
        }
    }
}


@Composable
private fun SortMenuPreviewContent() {
    Box(Modifier.padding(16.dp)) {
        SortMenu(current = SortList.DEFAULT, onSort = {})
    }
}

@Preview(name = "Light", showBackground = true, backgroundColor = 0xFFF7F9FF)
@Composable
private fun SortMenuLight() = BoardGameTheme(darkTheme = false) { SortMenuPreviewContent() }

@Preview(name = "Dark", showBackground = true, backgroundColor = 0xFF131313)
@Composable
private fun SortMenuDark() = BoardGameTheme(darkTheme = true) { SortMenuPreviewContent() }
