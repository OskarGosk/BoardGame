package com.goskar.boardgame.data.repository.mePlayer

/**
 * Persists the link between the logged-in account (userUID) and the [com.goskar.boardgame.data.models.Player]
 * that represents "me". There is no column joining User and Player in the DB, so the mapping is stored here.
 */
interface MePlayerRepository {

    /** Player id linked to the given account, or null if the user hasn't picked themselves yet. */
    suspend fun getLinkedPlayerId(userUID: String): String?

    suspend fun linkPlayer(userUID: String, playerId: String)

    suspend fun clear(userUID: String)
}
