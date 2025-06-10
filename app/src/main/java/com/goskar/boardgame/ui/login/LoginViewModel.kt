package com.goskar.boardgame.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.goskar.boardgame.data.models.User
import com.goskar.boardgame.data.repository.firebase.BoardGameFirebaseDataRepository
import com.goskar.boardgame.data.repository.user.UserRepository
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.data.useCase.UpsertAllGameUseCase
import com.goskar.boardgame.data.useCase.UpsertAllHistoryGameUseCase
import com.goskar.boardgame.data.useCase.UpsertAllPlayerUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class LoginState(
    val login: String = "",
    val password: String = "",
    val keyValue: String? = null,
    val userUID: String? = null,
    val successLogin: Boolean = false,
    val isLoggedIn: Boolean = false,
    val isLoading: Boolean = false,
    val isSuccessDownloadData: Boolean = false
)

class LoginViewModel(
    private val userSession: UserRepository,
    private val api: BoardGameFirebaseDataRepository,
    private val addAllGameToDb: UpsertAllGameUseCase,
    private val addAllPlayerToDb: UpsertAllPlayerUseCase,
    private val addAllHistoryToDb: UpsertAllHistoryGameUseCase,
) : ViewModel() {

    val auth = FirebaseAuth.getInstance()

    var user by mutableStateOf<FirebaseUser?>(null)
        private set

    var authError by mutableStateOf<String?>(null)
        private set

    init {
        user = auth.currentUser
    }

    fun checkIfLoggedIn(){
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }
            user = auth.currentUser
            if (user != null) {
                try {
                    val result = user!!.getIdToken(true).await()
                    val token = result.token

                    _state.update {
                        it.copy(
                            keyValue = token,
                            userUID = user?.uid,
                            isLoggedIn = true,
                            isLoading = false
                        )
                    }

                    getCurrentToken()
                } catch (e: Exception) {
                    _state.update { it.copy(isLoggedIn = false, isLoading = false) }
                }
            } else {
                signOut()
                _state.update { it.copy(isLoggedIn = false, isLoading = false) }
            }
        }
    }

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    fun update(state: LoginState) {
        _state.update { state }
    }

    fun signIn() {
        _state.update {
            it.copy(isLoading = true)
        }
        auth.signInWithEmailAndPassword(_state.value.login, _state.value.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    user = auth.currentUser

                    user?.getIdToken(true)
                        ?.addOnSuccessListener { result ->
                            val token = result.token
                            authError = null

                            _state.update {
                                it.copy(
                                    successLogin = true,
                                    keyValue = token,
                                    userUID = user?.uid,
                                    isLoading = false
                                )
                            }
                            getCurrentToken()
                        }
                        ?.addOnFailureListener { exception ->
                            authError = exception.message
                            _state.update { it.copy(isLoading = false) }
                        }

                } else {
                    authError = task.exception?.message
                    _state.update { it.copy(isLoading = false) }
                }
            }
    }

    fun signOut() {
        viewModelScope.launch {
            auth.signOut()
            user = null
            userSession.logout()
        }
    }
//
//    fun sendEmailVerification() {
//        auth.currentUser?.sendEmailVerification()
//    }
//
//    fun createAccount(email: String, password: String) {
//        auth.createUserWithEmailAndPassword(email, password)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    user = auth.currentUser
//                    authError = null
//                } else {
//                    authError = task.exception?.message
//                }
//            }
//    }

    private fun getCurrentToken() {
        viewModelScope.launch {
            userSession.logIn(User(email = _state.value.login, token = _state.value.keyValue, userUID = _state.value.userUID))
        }
    }

    private fun getAllData() {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }
            val response = attemptToTakeAllData(
                firstAction = ::getAllGame,
                secondAction = ::getAllPlayer,
                thirdAction = ::getAllHistory
            )
            _state.update {
                it.copy(
                    isLoading = false,
                    isSuccessDownloadData = response
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
            val response = addAllHistoryToDb.invoke(allHistory.data)
            return response
        } else return false
    }

    private suspend fun attemptToTakeAllData(
        firstAction: suspend () -> Boolean,
        secondAction: suspend () -> Boolean,
        thirdAction: suspend () -> Boolean,
    ): Boolean {
        if (firstAction()) {
            if (secondAction()) {
                if (thirdAction()) {
                    return true
                } else return false
            } else return false
        } else return false
    }
}