package com.goskar.boardgame.ui.components.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goskar.boardgame.data.repository.dbRepository.PlayerDbRepository
import com.goskar.boardgame.data.repository.mePlayer.MePlayerRepository
import com.goskar.boardgame.data.repository.user.UserRepository
import com.goskar.boardgame.data.rest.RequestResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Single source of truth for the current user's avatar initials, shown in every top bar.
 * Resolves from the Player linked to the account (see MePlayerRepository), falling back to the
 * account e-mail, so the avatar is identical on every screen.
 */
class CurrentUserViewModel(
    private val userSession: UserRepository,
    private val playerDbRepository: PlayerDbRepository,
    private val mePlayerRepository: MePlayerRepository,
) : ViewModel() {

    private val _initials = MutableStateFlow("")
    val initials = _initials.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            val user = userSession.getCurrentSession()
            val uid = user?.userUID
            val value = when {
                uid == null -> ""
                uid == "guest" -> "G"
                else -> {
                    val linkedId = mePlayerRepository.getLinkedPlayerId(uid)
                    val me = (playerDbRepository.getAllPlayer() as? RequestResult.Success)
                        ?.data?.firstOrNull { it.id == linkedId }
                    initialsOf(me?.name ?: user.email?.substringBefore("@").orEmpty())
                }
            }
            _initials.update { value }
        }
    }

    private fun initialsOf(name: String): String =
        name.trim().split(Regex("\\s+")).mapNotNull { it.firstOrNull() }.take(2)
            .joinToString("").ifBlank { name.take(2) }.uppercase()
}
