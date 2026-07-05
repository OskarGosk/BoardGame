package com.goskar.boardgame.ui.screens.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.goskar.boardgame.data.repository.user.UserRepository
import com.goskar.boardgame.data.useCase.ClearDbUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MedalItem(
    val title: String,
    val description: String,
)

data class ProfileNewState(
    val name: String = "Julian Thorne",
    val subtitle: String = "Dungeon Master Level 42",
    val initials: String = "JT",
    val gamesLogged: String = "148",
    val winRate: String = "64%",
    val notificationsActive: Boolean = true,
    val lastSynced: String = "Last synced: 2m ago",
    val medals: List<MedalItem> = listOf(
        MedalItem("Tactical Genius", "Won 5 games of Scythe"),
        MedalItem("Social Butterfly", "Played with 20 unique players"),
    ),
    val signedOut: Boolean = false,
)

class ProfileNewViewModel(
    private val userSession: UserRepository,
    private val clearDbUseCase: ClearDbUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileNewState())
    val state = _state.asStateFlow()

    fun signOut() {
        viewModelScope.launch {
            FirebaseAuth.getInstance().signOut()
            userSession.logout()
            clearDbUseCase.invoke()
            _state.update { it.copy(signedOut = true) }
        }
    }
}
