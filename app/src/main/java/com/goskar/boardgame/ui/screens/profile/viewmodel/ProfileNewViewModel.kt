package com.goskar.boardgame.ui.screens.profile.viewmodel

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.goskar.boardgame.R
import com.goskar.boardgame.data.repository.dbRepository.GamesHistoryDbRepository
import com.goskar.boardgame.data.repository.dbRepository.PlayerDbRepository
import com.goskar.boardgame.data.repository.mePlayer.MePlayerRepository
import com.goskar.boardgame.data.repository.syncState.SyncStateRepository
import com.goskar.boardgame.data.repository.user.UserRepository
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.data.useCase.BackupGuestUseCase
import com.goskar.boardgame.data.useCase.ClearDbUseCase
import com.goskar.boardgame.data.useCase.GetSyncStatusUseCase
import com.goskar.boardgame.data.useCase.UploadToCloudUseCase
import com.goskar.boardgame.ui.components.other.AppSnackBarType
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MedalItem(
    val title: String,
    val description: String,
)

data class PlayerPick(
    val id: String,
    val name: String,
    val initials: String,
)

enum class SignOutPrompt { None, UnsyncedData, SyncFailed }

data class ProfileNewState(
    val isLoading: Boolean = false,
    val isGuest: Boolean = false,
    val name: String = "",
    val subtitle: String = "",
    val initials: String = "",
    val gamesLogged: String = "0",
    val winRate: String = "—",
    val notificationsActive: Boolean = true,
    val lastSynced: String = "",
    val syncing: Boolean = false,
    val lastSyncedAt: Long? = null,
    val hasUnsyncedChanges: Boolean = false,
    val signOutPrompt: SignOutPrompt = SignOutPrompt.None,
    val medals: List<MedalItem> = emptyList(),
    val signedOut: Boolean = false,
)

sealed interface ProfileEvent {
    data class ShowMessage(@StringRes val message: Int, val type: AppSnackBarType) : ProfileEvent
}

class ProfileNewViewModel(
    private val userSession: UserRepository,
    private val playerDbRepository: PlayerDbRepository,
    private val historyRepository: GamesHistoryDbRepository,
    private val mePlayerRepository: MePlayerRepository,
    private val clearDbUseCase: ClearDbUseCase,
    private val uploadToCloud: UploadToCloudUseCase,
    private val backupGuest: BackupGuestUseCase,
    private val getSyncStatus: GetSyncStatusUseCase,
    private val syncState: SyncStateRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileNewState())
    val state = _state.asStateFlow()

    private val _events = Channel<ProfileEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val user = userSession.getCurrentSession()
            val uid = user?.userUID

            if (uid == null || uid == "guest") {
                loadGuest(isGuestAccount = true)
            } else {
                loadForUser(uid, user.email)
            }
            refreshSyncStatus()
            _state.update { it.copy(isLoading = false) }
        }
    }

    private suspend fun loadForUser(uid: String, email: String?) {
        val players = (playerDbRepository.getAllPlayer() as? RequestResult.Success)?.data ?: emptyList()
        val linkedId = mePlayerRepository.getLinkedPlayerId(uid)
        val me = players.firstOrNull { it.id == linkedId }

        if (me != null) {
            // winRatio on Player is a win count; games is the play count.
            val winRate = if (me.games > 0) "${me.winRatio * 100 / me.games}%" else "—"
            _state.update {
                it.copy(
                    isGuest = false,
                    name = me.name,
                    subtitle = email.orEmpty(),
                    initials = initialsOf(me.name),
                    gamesLogged = me.games.toString(),
                    winRate = winRate,
                )
            }
        } else {
            loadGuest(isGuestAccount = false)
        }
    }

    private suspend fun loadGuest(isGuestAccount: Boolean) {
        val sessions = (historyRepository.getAllHistoryGame() as? RequestResult.Success)?.data?.size ?: 0
        _state.update {
            it.copy(
                isGuest = isGuestAccount,
                name = "Guest",
                subtitle = "Guest session",
                initials = "G",
                gamesLogged = sessions.toString(),
                winRate = "—",
                notificationsActive = false,
            )
        }
    }

    fun forceSync() {
        if (state.value.syncing) return
        viewModelScope.launch {
            _state.update { it.copy(syncing = true) }
            val ok = runSync()
            _state.update {
                it.copy(
                    syncing = false,
                    lastSynced = if (ok) "Synced just now" else it.lastSynced,
                )
            }
            if (ok) refreshSyncStatus()
            _events.send(
                if (ok) ProfileEvent.ShowMessage(R.string.success_global, AppSnackBarType.SUCCESS)
                else ProfileEvent.ShowMessage(R.string.error_global, AppSnackBarType.ERROR)
            )
        }
    }

    fun requestSignOut() {
        viewModelScope.launch {
            if (getSyncStatus().hasUnsyncedChanges) {
                _state.update { it.copy(signOutPrompt = SignOutPrompt.UnsyncedData) }
            } else {
                signOut()
            }
        }
    }

    fun syncAndSignOut() {
        if (state.value.syncing) return
        viewModelScope.launch {
            _state.update { it.copy(syncing = true, signOutPrompt = SignOutPrompt.None) }
            val ok = runSync()
            _state.update { it.copy(syncing = false) }
            if (ok) signOut() else _state.update { it.copy(signOutPrompt = SignOutPrompt.SyncFailed) }
        }
    }

    fun signOutWithoutSync() {
        _state.update { it.copy(signOutPrompt = SignOutPrompt.None) }
        viewModelScope.launch { signOut() }
    }

    fun dismissSignOutPrompt() {
        _state.update { it.copy(signOutPrompt = SignOutPrompt.None) }
    }

    private suspend fun signOut() {
        FirebaseAuth.getInstance().signOut()
        userSession.logout()
        clearDbUseCase.invoke()
        syncState.clear()
        _state.update { it.copy(signedOut = true) }
    }

    private suspend fun runSync(): Boolean =
        if (state.value.isGuest) backupGuest() else uploadToCloud()

    private suspend fun refreshSyncStatus() {
        val status = getSyncStatus()
        _state.update {
            it.copy(hasUnsyncedChanges = status.hasUnsyncedChanges, lastSyncedAt = status.lastSyncedAt)
        }
    }

    private fun initialsOf(value: String): String =
        value.trim().split(Regex("\\s+")).mapNotNull { it.firstOrNull() }.take(2)
            .joinToString("").ifBlank { value.take(2) }.uppercase()
}
