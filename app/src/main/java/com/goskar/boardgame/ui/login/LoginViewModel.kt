package com.goskar.boardgame.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.goskar.boardgame.data.models.User
import com.goskar.boardgame.data.repository.user.UserRepository
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
    val isLoggedIn: Boolean = false
)

class LoginViewModel(
    private val userSession: UserRepository
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
            user = auth.currentUser
            if (user != null) {
                try {
                    val result = user!!.getIdToken(true).await()
                    val token = result.token

                    _state.update {
                        it.copy(
                            keyValue = token,
                            userUID = user?.uid,
                            isLoggedIn = true
                        )
                    }

                    getCurrentToken()
                } catch (e: Exception) {
                    _state.update { it.copy(isLoggedIn = false) }
                }
            } else {
                _state.update { it.copy(isLoggedIn = false) }
            }
        }
    }

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    fun update(state: LoginState) {
        _state.update { state }
    }

    fun signIn() {
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
                                    userUID = user?.uid
                                )
                            }
                            getCurrentToken()
                        }
                        ?.addOnFailureListener { exception ->
                            authError = exception.message
                        }

                } else {
                    authError = task.exception?.message
                }
            }
    }

    private fun signOut() {
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
}