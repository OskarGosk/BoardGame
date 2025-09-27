package com.goskar.boardgame.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.goskar.boardgame.R
import com.goskar.boardgame.data.repository.firebase.BoardGameFirebaseDataRepository
import com.goskar.boardgame.data.repository.user.UserRepository
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.data.useCase.ClearDbUseCase
import com.goskar.boardgame.data.useCase.UpsertAllGameUseCase
import com.goskar.boardgame.data.useCase.UpsertAllHistoryGameExpansionUseCase
import com.goskar.boardgame.data.useCase.UpsertAllHistoryGameUseCase
import com.goskar.boardgame.data.useCase.UpsertAllPlayerUseCase
import com.goskar.boardgame.ui.home.components.OtherBottomMenuList
import com.goskar.boardgame.utils.convertHistoryGameListToDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

data class HomeScreenState(
    val isLoading: Boolean = false,
    val isSuccessDownloadData: Boolean = false,
    val isSignOut: Boolean = false,
)

@KoinViewModel
class HomeScreenViewModel(
    private val api: BoardGameFirebaseDataRepository,
    private val addAllGameToDb: UpsertAllGameUseCase,
    private val addAllPlayerToDb: UpsertAllPlayerUseCase,
    private val addAllHistoryToDb: UpsertAllHistoryGameUseCase,
    private val addAllHistoryGameExpansionToDb: UpsertAllHistoryGameExpansionUseCase,
    private val userSession: UserRepository,
    private val clearDbUseCase: ClearDbUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeScreenState())
    var state = _state.asStateFlow()

    val otherItems = MutableStateFlow(
        listOf(
            OtherBottomMenuList(
                R.drawable.icons_shutdown, R.string.signOut, ::signOut
            ),
            OtherBottomMenuList(
                R.drawable.icons_download, R.string.download, ::getAllData
            )
        )
    )

    private val auth = FirebaseAuth.getInstance()

    var user by mutableStateOf<FirebaseUser?>(null)
        private set

    init {
        user = auth.currentUser
    }


    fun getAllData() {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }
            _state.update {
                it.copy(
                    isLoading = false,
                    isSuccessDownloadData = attemptToTakeAllData(
                        firstAction = ::getAllGame,
                        secondAction = ::getAllPlayer,
                        thirdAction = ::getAllHistory,
                        fourthAction = ::getAllHistoryExpansion
                    )
                )
            }
        }
    }

    suspend fun getAllGame(): Boolean {
        val allGame = api.getAllGame()

        if (allGame is RequestResult.Success) {
            val response = addAllGameToDb.invoke(allGame.data)
            return response
        } else return false
    }

    suspend fun getAllPlayer(): Boolean {
        val allPlayer = api.getAllPlayer()

        if (allPlayer is RequestResult.Success) {
            val response = addAllPlayerToDb.invoke(allPlayer.data)
            return response
        } else return false
    }

    suspend fun getAllHistory(): Boolean {
        val allHistory = api.getAllHistoryGame()

        if (allHistory is RequestResult.Success) {
            val response = addAllHistoryToDb.invoke((convertHistoryGameListToDto(allHistory.data)))
            return response
        } else return false
    }

    suspend fun getAllHistoryExpansion(): Boolean {
        val allHistoryExpansion = api.getAllHistoryGameExpansion()

        if (allHistoryExpansion is RequestResult.Success) {
            val response = addAllHistoryGameExpansionToDb.invoke((allHistoryExpansion.data))
            return response
        } else return false
    }

    private suspend fun attemptToTakeAllData(
        firstAction: suspend () -> Boolean,
        secondAction: suspend () -> Boolean,
        thirdAction: suspend () -> Boolean,
        fourthAction: suspend () -> Boolean,
        ): Boolean {
        return if (firstAction()) {
            if (secondAction()) {
                if(thirdAction()) {
                    fourthAction()
                } else false
            } else false
        } else false
    }

    fun signOut() {
        viewModelScope.launch {
            auth.signOut()
            user = null
            userSession.logout()

            clearDbUseCase.invoke()

            _state.update {
                it.copy(
                    isSignOut = true
                )
            }
        }
    }

}