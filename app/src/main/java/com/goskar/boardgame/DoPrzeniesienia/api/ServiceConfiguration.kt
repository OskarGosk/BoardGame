package OGosk.boardgamebase.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

//object ServiceConfiguration {
//    private var retrofit = Retrofit.Builder()
//        .baseUrl("https://boardgame-18024-default-rtdb.europe-west1.firebasedatabase.app/")
//        .addConverterFactory(GsonConverterFactory.create())
//        .build()
//
//    val gameService: GameService = retrofit.create(GameService::class.java)
//    val playerService: PlayerService = retrofit.create(PlayerService::class.java)
//    val historyGameService: HistoryGameService = retrofit.create(HistoryGameService::class.java)
//}