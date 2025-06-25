package com.goskar.boardgame.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.theme.SmoochBold24LetterSpacing2


@Composable
fun OtherBottomMenu(
    items: List<OtherBottomMenuList>?,
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        if (isExpanded && !items.isNullOrEmpty()) {
            LazyColumn(
                modifier = Modifier,
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.End
            ) {
                items(items.size) {
                    OtherBottomMenuItem(
                        icon = items[it].icon,
                        name = items[it].name,
                        onClick = items[it].onClick
                    )
                }
            }
        }

        Button(
            shape = CutCornerShape(percent = 10),
            onClick = { isExpanded = !isExpanded },
            modifier = Modifier
                .padding(vertical = 12.dp)
                .height(48.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.icons_more),
                    contentDescription = stringResource(R.string.other),
                    Modifier
                        .padding(end = 12.dp)
                        .size(25.dp)

                )
                Text(
                    text = stringResource(R.string.other),
                    style = SmoochBold24LetterSpacing2,
                )
            }
        }
    }
}

@Composable
fun OtherBottomMenuItem(icon: Int, name: Int, onClick: () -> Unit = {}) {

    Button(
        shape = CutCornerShape(percent = 10),
        onClick = { onClick() },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .height(48.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = stringResource(name),
                Modifier
                    .padding(end = 12.dp)
                    .size(25.dp)

            )
            Text(
                text = stringResource(name),
                style = SmoochBold24LetterSpacing2,
            )
        }
}
}

@Preview(showBackground = true)
@Composable
fun OtherBottomMenuPreview() {
    val items = listOf(
        OtherBottomMenuList(R.drawable.icons_shutdown, R.string.other),
        OtherBottomMenuList(R.drawable.icons_logout, R.string.delete)
    )

    OtherBottomMenu(items)
}