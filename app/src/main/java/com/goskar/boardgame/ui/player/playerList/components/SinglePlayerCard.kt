package com.goskar.boardgame.ui.player.playerList.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import com.goskar.boardgame.data.rest.models.Player
import com.goskar.boardgame.ui.player.addEditPlayer.AddEditPlayerScreen

@Composable
fun SinglePlayerCard(
    player: Player,
    deletePlayer: (String)-> Unit = {},
    refreshPlayer: ()-> Unit = {}) {
    var isExpanded by remember { mutableStateOf(false) }
    val navigator = LocalNavigator.current
    Card (
        shape = RoundedCornerShape(25),
        modifier = Modifier.clickable {
            isExpanded = !isExpanded
        }
            .padding(bottom = 10.dp)
    ){
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = player.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp,
                    modifier = Modifier.padding(10.dp)
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier
                        .size(30.dp)
                )
            }
            WinLinearIndicator(
                progress = (player.winRatio.toFloat() / player.games),
                modifier = Modifier,
                isExpanded = isExpanded
            )
        }
        if(isExpanded) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            )
            {
                IconButton(onClick = {
                    navigator?.push(AddEditPlayerScreen(player))
                    isExpanded = false
                }) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Game")
                }
                IconButton(onClick = {
                    deletePlayer(player.id)
                    refreshPlayer()
                    isExpanded = false
                }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Game"
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun SinglePlayerCardPreview() {
    val player =
        Player(name = "Oskar", winRatio = 2, games = 6, description = "ds", selected = true)

    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        Box (modifier = Modifier.padding(10.dp)){
            SinglePlayerCard(player)
        }
    }
}