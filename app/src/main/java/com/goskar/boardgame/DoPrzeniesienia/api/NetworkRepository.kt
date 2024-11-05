package OGosk.boardgamebase.api

import OGosk.boardgamebase.model.Game
import OGosk.boardgamebase.model.GameIdRespons
import OGosk.boardgamebase.model.HistoryGame
import OGosk.boardgamebase.model.HistoryIdResponse
import OGosk.boardgamebase.model.Player
import OGosk.boardgamebase.model.PlayerIdRespons

//class PlayerNetworkRepository (private val playerService: PlayerService) {
//
//    suspend fun addPlayer(player: Player):PlayerIdRespons {
//        return playerService.add(player)
//    }
//
//    suspend fun getAllPlayer(): List<Player>{
//        return playerService.getAll().map { element ->
//            element.value.copy(id = element.key)
//        }
//    }
//
//    suspend fun deletePlayer (playerId: String){
//        playerService.deletePlayer(playerId)
//    }
//
//    suspend fun editPlayer (player: Player){
//        playerService.editPlayer(player.id, player)
//    }
//}
//
//class GameNetworkRepository (private val gameService: GameService) {
//
//    suspend fun addGame(game: Game):GameIdRespons {
//        return gameService.add(game)
//    }
//
//    suspend fun getAllGame(): List<Game>{
//        return  gameService.getAll().map { element ->
//            element.value.copy(id = element.key)
//        }
//    }
//
//    suspend fun deleteGame (gameId: String){
//        gameService.deleteGame(gameId)
//    }
//
//    suspend fun editGame (game: Game) {
//        gameService.editGame(game.id, game)
//    }
//}
//
//class HistoryGameNetworkRepository (private val historyGameService: HistoryGameService) {
//
//    suspend fun addHistoryGame(historyGame: HistoryGame):HistoryIdResponse {
//        return historyGameService.add(historyGame)
//    }
//
//    suspend fun getAll(): List<HistoryGame>{
//        return historyGameService.getAll().map { element ->
//            element.value.copy(id = element.key)
//        }
//    }
//
//    suspend fun deleteHistoryGame (historyGameId: String){
//        historyGameService.deleteHistoryGame(historyGameId)
//    }
//
//    suspend fun editHistoryGame (historyGame: HistoryGame) {
//        historyGameService.editHistoryGame(historyGame.id, historyGame)
//    }
//}