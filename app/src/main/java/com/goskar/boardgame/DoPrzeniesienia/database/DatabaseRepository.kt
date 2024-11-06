package OGosk.boardgamebase.database

import com.goskar.boardgame.data.di.Db
import com.goskar.boardgame.data.rest.models.Game
import com.goskar.boardgame.data.rest.models.HistoryGame
import com.goskar.boardgame.data.rest.models.Player


class PlayerDatabaseRepository (private val db: Db){

    suspend fun insertPlayer(player: Player) {
        db.playerDao().insert(player)
    }

    suspend fun insertAllPlayer(playerList: List<Player>) {
        db.playerDao().insertAll(playerList)
    }

    suspend fun getAllPlayers(): List<Player> {
        return db.playerDao().getAll()
    }

    suspend fun deletePlayer(player: Player) {
        db.playerDao().delete(player)
    }

    suspend fun editPlayer(player: Player) {
        db.playerDao().edit(player)
    }
}

class GameDatabaseRepository (private val db: Db) {

    suspend fun insertGame(game: Game) {
        db.gameDao().insert(game)
    }

    suspend fun insertAllGame(gameList: List<Game>){
        db.gameDao().insertAll(gameList)
    }

    suspend fun getAllGame(): List<Game> {
        return db.gameDao().getAll()
    }

    suspend fun deleteGame(game: Game) {
        db.gameDao().delete(game)
    }

    suspend fun editGame(game: Game) {
        db.gameDao().edit(game)
    }
}

class HistoryGameDatabaseRepository (private val db: Db) {

    suspend fun insertHistoryGame(historyGame: HistoryGame) {
        db.historyGameDao().insert(historyGame)
    }

    suspend fun insertAllHistoryGame(historyGameList: List<HistoryGame>){
        db.historyGameDao().insertAll(historyGameList)
    }

    suspend fun getAllHistoryGame():List<HistoryGame> {
        return db.historyGameDao().getAll()
    }

    suspend fun deleteHistoryGame(historyGame: HistoryGame) {
        db.historyGameDao().delete(historyGame)
    }

    suspend fun editHistoryGame(historyGame: HistoryGame) {
        db.historyGameDao().edit(historyGame)
    }
}