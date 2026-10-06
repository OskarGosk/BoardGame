package com.goskar.boardgame.ui.login

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.goskar.boardgame.R
import com.goskar.boardgame.data.models.User
import com.goskar.boardgame.data.repository.syncState.SyncStateRepository
import com.goskar.boardgame.data.repository.user.UserRepository
import com.goskar.boardgame.data.useCase.ClearDbUseCase
import com.goskar.boardgame.data.useCase.GetLocalSnapshotUseCase
import com.goskar.boardgame.ui.components.other.AppSnackBarType
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed interface LoginEvent {
    data class ShowMessage(
        @StringRes val message: Int,
        val type: AppSnackBarType,
        val mergeLocalData: Boolean = false,
    ) : LoginEvent
    data class ShowErrorMessage(val message: String?) : LoginEvent
    object LoggedInOrGuest : LoginEvent
}

data class LocalDataSummary(
    val gameCount: Int,
    val playerCount: Int,
    val sessionCount: Int,
)

data class LoginState(
    val login: String = "",
    val password: String = "",
    val loginError: Boolean = false,
    val passwordError: Boolean = false,
    val keyValue: String? = null,
    val userUID: String? = null,
    val isLoggedIn: Boolean = false,
    val isLoading: Boolean = false,
    val isSuccessDownloadData: Boolean = false,
    val pendingLocalData: LocalDataSummary? = null,
)

class LoginViewModel(
    private val userSession: UserRepository,
    private val getSnapshot: GetLocalSnapshotUseCase,
    private val clearDb: ClearDbUseCase,
    private val syncState: SyncStateRepository,
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    var user by mutableStateOf<FirebaseUser?>(null)
        private set

    private var authError by mutableStateOf<String?>(null)
        private set

    private fun signedInUser(): FirebaseUser? = auth.currentUser?.takeUnless { it.isAnonymous }

    init {
        user = signedInUser()
    }

    fun checkIfLoggedIn() {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }
            user = signedInUser()
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

                    getCurrentToken(signIn = false)
                } catch (e: Exception) {
                    _state.update { it.copy(isLoggedIn = false, isLoading = false) }
                }
            } else {
                val questAccount = userSession.getCurrentSession()
                if (questAccount?.userUID == "guest") {
                    _state.update { it.copy(isLoggedIn = true, isLoading = false) }
                } else {
                    signOut()
                    _state.update { it.copy(isLoggedIn = false, isLoading = false) }
                }
            }
        }
    }

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    private val _events = Channel<LoginEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()


    fun updateLogin(value: String) {
        _state.update { it.copy(login = value, loginError = false) }
    }

    fun updatePassword(value: String) {
        _state.update { it.copy(password = value, passwordError = false) }
    }
    fun signIn() {
        val loginBlank = _state.value.login.isBlank()
        val passwordBlank = _state.value.password.isBlank()
        if (loginBlank || passwordBlank) {
            _state.update { it.copy(loginError = loginBlank, passwordError = passwordBlank) }
            return
        }
        _state.update {
            it.copy(isLoading = true, loginError = false, passwordError = false)
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
                                    keyValue = token,
                                    userUID = user?.uid,
                                    isLoading = false
                                )
                            }
                            continueAfterSignIn()

                        }
                        ?.addOnFailureListener { exception ->
                            authError = exception.message
                            _state.update { it.copy(isLoading = false) }
                            _events.trySend(
                                LoginEvent.ShowErrorMessage(
                                    authError
                                )
                            )
                        }

                } else {
                    authError = task.exception?.message
                    _state.update { it.copy(isLoading = false) }
                    _events.trySend(
                        LoginEvent.ShowErrorMessage(
                            authError
                        )
                    )
                }
            }
    }

    internal fun continueAfterSignIn() {
        viewModelScope.launch {
            val local = getSnapshot()
            if (local.isEmpty) {
                finishSignIn(mergeLocalData = false)
            } else {
                _state.update {
                    it.copy(
                        pendingLocalData = LocalDataSummary(
                            gameCount = local.games.size,
                            playerCount = local.players.size,
                            sessionCount = local.history.size,
                        ),
                    )
                }
            }
        }
    }

    fun mergeLocalDataIntoAccount() {
        viewModelScope.launch { finishSignIn(mergeLocalData = true) }
    }

    fun discardLocalData() {
        viewModelScope.launch {
            if (!clearDb()) {
                _events.send(LoginEvent.ShowErrorMessage(null))
                return@launch
            }
            finishSignIn(mergeLocalData = false)
        }
    }

    fun cancelSignIn() {
        auth.signOut()
        user = null
        _state.update { it.copy(pendingLocalData = null, keyValue = null, userUID = null) }
    }

    private suspend fun finishSignIn(mergeLocalData: Boolean) {
        _state.update { it.copy(pendingLocalData = null) }
        syncState.clear()
        saveSessionAndNotify(signIn = true, mergeLocalData = mergeLocalData)
    }

    fun signOut() {
        viewModelScope.launch {
            auth.signOut()
            user = null
            userSession.logout()
        }
    }

    fun questAccount() {
        _state.update {
            it.copy(
                login = "guest",
                keyValue = "guest",
                userUID = "guest",
                isLoading = false
            )
        }
        getCurrentToken(signIn = false)
    }

    private fun getCurrentToken(signIn: Boolean = false) {
        viewModelScope.launch { saveSessionAndNotify(signIn = signIn, mergeLocalData = false) }
    }

    private suspend fun saveSessionAndNotify(signIn: Boolean, mergeLocalData: Boolean) {
        userSession.logIn(
            User(
                email = _state.value.login,
                token = _state.value.keyValue,
                userUID = _state.value.userUID
            )
        )
        if (signIn) {
            _events.send(
                LoginEvent.ShowMessage(
                    R.string.success_global,
                    AppSnackBarType.SUCCESS,
                    mergeLocalData,
                )
            )
            return
        }
        _events.send(LoginEvent.LoggedInOrGuest)
    }

}
