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
import com.goskar.boardgame.data.repository.user.UserRepository
import com.goskar.boardgame.ui.components.other.AppSnackBarType
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed interface LoginEvent {
    data class ShowMessage(@StringRes val message: Int, val type: AppSnackBarType) : LoginEvent
    object loggedInOrGuest : LoginEvent
}

data class LoginState(
    val login: String = "",
    val password: String = "",
    val keyValue: String? = null,
    val userUID: String? = null,
    val isLoggedIn: Boolean = false,
    val isLoading: Boolean = false,
    val isSuccessDownloadData: Boolean = false
)

class LoginViewModel(
    private val userSession: UserRepository,
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    var user by mutableStateOf<FirebaseUser?>(null)
        private set

    private var authError by mutableStateOf<String?>(null)
        private set

    init {
        user = auth.currentUser
    }

    fun checkIfLoggedIn() {
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
                                    keyValue = token,
                                    userUID = user?.uid,
                                    isLoading = false
                                )
                            }
                            getCurrentToken(signIn = true)

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
        viewModelScope.launch {
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
                        AppSnackBarType.SUCCESS
                    )
                )
                return@launch
            }
            _events.send(
                LoginEvent.loggedInOrGuest
            )
        }
    }

}