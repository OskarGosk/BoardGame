package OGosk.boardgamebase.viewModel

//import OGosk.boardgamebase.api.GameNetworkRepository
//import OGosk.boardgamebase.database.GameDatabaseRepository
//import OGosk.boardgamebase.model.Game
//import OGosk.boardgamebase.model.GameIdRespons
//import OGosk.boardgamebase.model.OperationStatus
//import android.util.Log
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.setValue
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import kotlinx.coroutines.launch
//
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