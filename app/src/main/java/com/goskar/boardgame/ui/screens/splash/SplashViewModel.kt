package com.goskar.boardgame.ui.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.goskar.boardgame.data.repository.user.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class SplashDestination { Home, Login }

class SplashViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    // State, not a one-shot event: the ViewModel outlives a configuration change, so a recreated
    // splash must still be able to read where to go after the decision was already made.
    private val _destination = MutableStateFlow<SplashDestination?>(null)
    val destination: StateFlow<SplashDestination?> = _destination.asStateFlow()

    init {
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        viewModelScope.launch {
            // Optional delay to show the splash icon for a moment
            delay(1000)

            val firebaseUser = FirebaseAuth.getInstance().currentUser?.takeUnless { it.isAnonymous }
            val isLogged = userRepository.isLoggedIn()

            _destination.value = if (firebaseUser != null || isLogged) {
                SplashDestination.Home
            } else {
                SplashDestination.Login
            }
        }
    }
}
