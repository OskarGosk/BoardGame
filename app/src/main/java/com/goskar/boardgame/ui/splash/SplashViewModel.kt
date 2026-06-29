package com.goskar.boardgame.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.goskar.boardgame.data.repository.user.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

sealed class SplashEvent {
    object NavigateToHome : SplashEvent()
    object NavigateToLogin : SplashEvent()
}

class SplashViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _events = MutableSharedFlow<SplashEvent>()
    val events = _events.asSharedFlow()

    init {
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        viewModelScope.launch {
            // Optional delay to show the splash icon for a moment
            delay(1000)
            
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            val isLogged = userRepository.isLoggedIn()
            
            if (firebaseUser != null || isLogged) {
                _events.emit(SplashEvent.NavigateToHome)
            } else {
                _events.emit(SplashEvent.NavigateToLogin)
            }
        }
    }
}
