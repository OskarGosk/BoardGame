package com.goskar.boardgame.ui.gamesList.lists.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.ui.theme.SmoochBold26

@Composable
fun SingleLineGame(
    game: Game,
    modifier: Modifier,
    deleteGame: (Game) -> Unit = {},
    refresh: () -> Unit = {},
) {

    Card(
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp),
        modifier = modifier
            .height(48.dp)
            .padding(5.dp)
    ) {
        Row {
            Text(
                text = game.name,
                style = SmoochBold26,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(horizontal = 5.dp)
                    .weight(2f)
            )
            ButtonRow(game = game, deleteGame, refresh, Modifier.weight(1f))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SingleLineGamePreview() {
    val game = Game(
        name = "Marvel Marvel ",
        expansion = false,
        cooperate = false,
        baseGame = "",
        minPlayer = "1",
        maxPlayer = "4",
        games = 3,
        id = "dasfgfsh"
    )
    SingleLineGame(game = game, Modifier)
}

@Preview(showBackground = true)
@Composable
fun SingleLineGame2Preview() {
    val game = Game(
        name = "Marvel Marvelale z bardzo długą nazwą jeszcze dłuższa",
        expansion = false,
        cooperate = false,
        baseGame = "",
        minPlayer = "1",
        maxPlayer = "4",
        games = 3,
        id = "dasfgfsh2"
    )
    SingleLineGame(game = game, Modifier)
}