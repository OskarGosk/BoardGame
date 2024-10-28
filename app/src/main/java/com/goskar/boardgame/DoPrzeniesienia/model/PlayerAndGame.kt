package OGosk.boardgamebase.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.time.LocalDate
import java.util.UUID

@Entity
data class Player(
    val name: String,
//    val image: Image,
    val games: Int,
    val winRatio: Int,
    val description: String,
    var selected: Boolean,
    @PrimaryKey val id: String = UUID.randomUUID().toString()
) : Serializable

@Entity
data class Game(
    val name: String,
    val expansion: Boolean,
    val baseGame: String,
    val minPlayer: String,
    val maxPlayer: String,
    val games: Int,
    @PrimaryKey val id: String = UUID.randomUUID().toString()
) : Serializable

@Entity
data class HistoryGame(
    val gameName: String,
    val winner: String,
    val gameData: String,
    val listOfPlayer: List<String>,
    val description: String,
    @PrimaryKey val id: String = UUID.randomUUID().toString()
) : Serializable