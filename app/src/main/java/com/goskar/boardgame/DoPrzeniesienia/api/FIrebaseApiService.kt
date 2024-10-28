package OGosk.boardgamebase.api

import OGosk.boardgamebase.model.Game
import OGosk.boardgamebase.model.GameIdRespons
import OGosk.boardgamebase.model.HistoryGame
import OGosk.boardgamebase.model.HistoryIdResponse
import OGosk.boardgamebase.model.Player
import OGosk.boardgamebase.model.PlayerIdRespons
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path


interface GameService {
    @POST("game.json")
    suspend fun add(@Body game: Game): GameIdRespons

    @GET("game.json")
    suspend fun getAll(): Map<String, Game>

    @DELETE("game/{id}.json")
    suspend fun deleteGame(@Path("id") gameId: String)

    @PUT("game/{id}.json")
    suspend fun editGame(
        @Path("id") gameId: String,
        @Body game: Game
    )
}
interface PlayerService {

    @POST("player.json")
    suspend fun add(@Body player: Player): PlayerIdRespons

    @GET("player.json")
    suspend fun getAll(): Map<String, Player>

    @DELETE("player/{id}.json")
    suspend fun deletePlayer(@Path("id") playerId: String)

    @PUT("player/{id}.json")
    suspend fun editPlayer(
        @Path("id") playerId: String,
        @Body player: Player
    )
}

interface HistoryGameService {

    @POST("historyGame.json")
    suspend fun add(@Body historyGame: HistoryGame) : HistoryIdResponse

    @GET("historyGame.json")
    suspend fun getAll(): Map<String, HistoryGame>

    @DELETE("historyGame/{id}.json")
    suspend fun deleteHistoryGame(@Path("id") historyId: String)

    @PUT("historyGame/{id{.json")
    suspend fun editHistoryGame(
        @Path("id") historyId: String,
        @Body historyGame: HistoryGame
    )
}