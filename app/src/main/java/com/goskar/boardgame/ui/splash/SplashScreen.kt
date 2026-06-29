package com.goskar.boardgame.ui.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.goskar.boardgame.ui.home.HomeScreen
import com.goskar.boardgame.ui.login.LoginScreen
import com.goskar.boardgame.ui.theme.BoardGameColors
import com.goskar.boardgame.ui.theme.BoardGameShapes
import org.koin.androidx.compose.koinViewModel

class SplashScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel: SplashViewModel = koinViewModel()
        val navigator = LocalNavigator.current

        LaunchedEffect(Unit) {
            viewModel.events.collect { event ->
                when (event) {
                    SplashEvent.NavigateToHome -> navigator?.replace(HomeScreen(false))
                    SplashEvent.NavigateToLogin -> navigator?.replace(LoginScreen())
                }
            }
        }

        SplashScreenContent()
    }
}

@Composable
fun SplashScreenContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF7F9FF),
                        Color(0xFFE3EFFF)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(BoardGameShapes.Large)
                .background(BoardGameColors.Primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Casino,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(60.dp)
            )
        }
    }
}
