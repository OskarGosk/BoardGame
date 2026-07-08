package com.goskar.boardgame.data.repository.mePlayer

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.mePlayerDataStore by preferencesDataStore(name = "me_player_link")

class MePlayerRepositoryImpl(
    private val context: Context,
) : MePlayerRepository {

    private fun key(userUID: String) = stringPreferencesKey("me_player_$userUID")

    override suspend fun getLinkedPlayerId(userUID: String): String? =
        context.mePlayerDataStore.data.map { it[key(userUID)] }.first()

    override suspend fun linkPlayer(userUID: String, playerId: String) {
        context.mePlayerDataStore.edit { it[key(userUID)] = playerId }
    }

    override suspend fun clear(userUID: String) {
        context.mePlayerDataStore.edit { it.remove(key(userUID)) }
    }
}
