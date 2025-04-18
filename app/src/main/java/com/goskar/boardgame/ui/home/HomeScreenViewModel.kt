package com.goskar.boardgame.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goskar.boardgame.data.repository.BoardGameApiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class HomeScreenViewModel(
    private val boardGameApiRepository: BoardGameApiRepository
) : ViewModel() {

    private val _image = MutableStateFlow("")
    var image = _image.asStateFlow()


    fun getAllData() {
        viewModelScope.launch {
            val response = boardGameApiRepository.searchGame("marvel")
            Log.d("Oskar22","$response")
//            _image.value = response?.boardGames?.first()?.image.toString()
//            Log.d("Oskar22","${_image.value}")

        }
    }

}