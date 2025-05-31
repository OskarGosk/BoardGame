package com.goskar.boardgame

import org.koin.core.qualifier.named

object Constants {
    const val API_URL: String = "https://board-game-goskar-default-rtdb.europe-west1.firebasedatabase.app/"
    const val API_URL_BOARD: String = "https://boardgamegeek.com/xmlapi/"
    val FIREBASE_RETROFIT = named("firebaseRetrofit")
    val BGG_RETROFIT = named("bggRetrofit")
    val FIREBASE_CLIENT = named("firebase")
    val BGG_CLIENT = named("bgg")
    const val DATABASE_NAME: String = "Board Game Data"
    const val GLOBAL_TAG: String = "Board Game"
}