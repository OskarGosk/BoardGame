package com.goskar.boardgame.ui.gamesList.lists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goskar.boardgame.data.repository.GameNetworkRepository
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.data.rest.models.Game
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

data class GameListState(
    val gameList: List<Game>? = emptyList<Game>(),
    val successDeleteGame: Boolean = false,
    val errorVisible: Boolean = false,
    val searchTxt: String = ""
)

@KoinViewModel
class GameListViewModel(
    private val gameNetworkRepository: GameNetworkRepository
) : ViewModel() {

    private val _state = MutableStateFlow(GameListState())
    val state = _state.asStateFlow()

    fun update(state: GameListState) {
        _state.update { state }
    }

    init {
        getAllGame()
    }

    fun getAllGame() {
        viewModelScope.launch {
            val response = gameNetworkRepository.getAllGame().toMutableList()
            _state.update {
                it.copy(
                    gameList = response
                )
            }
        }
    }

    fun validateDeleteGame(gameID: String){
        viewModelScope.launch {
            val response = gameNetworkRepository.deleteGame(gameId = gameID)
            when (response){
                is RequestResult.Success -> {
                    _state.update {
                        it.copy(
                            successDeleteGame = true
                        )
                    }
                    getAllGame()
                }
                else -> {
                    _state.update {
                        it.copy(
                            errorVisible = true
                        )
                    }
                }
            }
        }
    }
}



//class GameViewModel (
//    private var gameDatabaseRepository: GameDatabaseRepository,
//    private var gameNetworkRepository: GameNetworkRepository
//
//): ViewModel(){
//
//    var gameList by mutableStateOf(emptyList<Game>())
//    var getAllGameStatus by mutableStateOf(OperationStatus.UNKNOWN)
//    var addEditGameStatus by mutableStateOf(OperationStatus.UNKNOWN)
//
//    fun getAllGame (){
//        viewModelScope.launch {
//            try {
//                getAllGameStatus = OperationStatus.LOADING
//                gameList = gameNetworkRepository.getAllGame().toMutableList()
//                gameDatabaseRepository.insertAllGame(gameList)
//                getAllGameStatus = OperationStatus.SUCCESS
//            } catch (e: Exception) {
//                getAllGameStatus = OperationStatus.ERROR
//                Log.d ("Game", "Error in loading all Game --- $e")
//            }
//        }
//    }
//
//    fun addGame (game: Game) {
//        viewModelScope.launch {
//            try {
//                addEditGameStatus = OperationStatus.LOADING
//                val response: GameIdRespons = gameNetworkRepository.addGame(game)
//                gameDatabaseRepository.insertGame(game.copy(id = response.name))
//                addEditGameStatus = OperationStatus.SUCCESS
//            } catch (e: Exception) {
//                addEditGameStatus = OperationStatus.ERROR
//                Log.d("Game", "Nie udało się dodać gry --- $e")
//            }
//        }
//    }
//
//    fun deleteGame(game: Game) {
//        viewModelScope.launch {
//            try {
//                gameNetworkRepository.deleteGame(game.id)
//                gameDatabaseRepository.deleteGame(game)
//                removeGameFromList(game)
//            }catch (e:Exception) {
//                Log.d ("Game", "Nie udało się usunąć gry --- $e")
//            }
//        }
//    }
//
//    private fun removeGameFromList (game: Game) {
//        gameList.toMutableList().also {
//            it.remove(game)
//            gameList = it
//        }
//    }
//
//    fun editGame (game: Game) {
//        viewModelScope.launch {
//            try {
//                addEditGameStatus = OperationStatus.LOADING
//                gameNetworkRepository.editGame(game)
//                gameDatabaseRepository.editGame(game)
//                addEditGameStatus = OperationStatus.SUCCESS
//            } catch (e:Exception) {
//                addEditGameStatus = OperationStatus.ERROR
//                Log.d ("Game", "Nie edytowało gry --- $e")
//            }
//        }
//    }
//}