package com.goskar.boardgame.ui.gamesList.lists.components


import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.ui.theme.SmoochBold18


@Composable
fun FormatCard(
    modifier: Modifier,
    name: String,
    isSelected: Boolean = false,
    onClick: () -> Unit = {}
) {

    Card(
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if(isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        ),
        modifier = modifier
            .height(40.dp)
            .padding(5.dp)
            .clickable {
                onClick()
            }
    ) {
        Text(text = name,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(5.dp)
                .fillMaxWidth(),
            style = SmoochBold18,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FormatCardPreview() {
    FormatCard(Modifier, "Line")
}

@Preview(showBackground = true)
@Composable
fun FormatCardSelectedPreview() {
    FormatCard(Modifier, "Square", true)
}