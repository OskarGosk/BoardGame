package com.goskar.boardgame.ui.games.play

import androidx.lifecycle.ViewModel
import com.goskar.boardgame.data.rest.models.Game
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.koin.android.annotation.KoinViewModel

data class GamePLayState(
    val game : Game? = null
)

@KoinViewModel
class GamePlayViewModel (
//    private val
) : ViewModel() {

    private val _state = MutableStateFlow(GamePLayState())
    val state = _state.asStateFlow()

    fun update(state: GamePLayState) {
        _state.update { state }
    }

}