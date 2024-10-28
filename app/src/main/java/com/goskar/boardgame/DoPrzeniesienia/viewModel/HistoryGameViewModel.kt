package OGosk.boardgamebase.viewModel

import OGosk.boardgamebase.api.HistoryGameNetworkRepository
import OGosk.boardgamebase.database.HistoryGameDatabaseRepository
import OGosk.boardgamebase.model.Game
import OGosk.boardgamebase.model.HistoryGame
import OGosk.boardgamebase.model.HistoryIdResponse
import OGosk.boardgamebase.model.OperationStatus
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class HistoryGameViewModel (
    private var historyGameNetworkRepository: HistoryGameNetworkRepository,
    private var historyGameDatabaseRepository: HistoryGameDatabaseRepository
): ViewModel(){

    var historyGameList by mutableStateOf(emptyList<HistoryGame>())
    var getAllHistoryGameStatus by mutableStateOf(OperationStatus.UNKNOWN)
    var addEditHistoryGameStatus by mutableStateOf(OperationStatus.UNKNOWN)

    fun getHistoryGames () {
        viewModelScope.launch {
            try {
                getAllHistoryGameStatus = OperationStatus.LOADING
                historyGameList = historyGameNetworkRepository.getAll().toMutableList()
                getAllHistoryGameStatus = OperationStatus.SUCCESS
            } catch (e: Exception) {
                getAllHistoryGameStatus = OperationStatus.ERROR
                Log.d ("GameHistory", "Error in loading History Game --- $e")
            }
        }
    }

    fun addHistoryGame(historyGame: HistoryGame) {
        viewModelScope.launch {
            try {
                addEditHistoryGameStatus = OperationStatus.LOADING
                val response: HistoryIdResponse = historyGameNetworkRepository.addHistoryGame(historyGame)
                historyGameDatabaseRepository.insertHistoryGame(historyGame.copy(id = response.name))
                addEditHistoryGameStatus = OperationStatus.SUCCESS
            } catch (e: Exception) {
                addEditHistoryGameStatus = OperationStatus.ERROR
                Log.d ("GameHistory", "Error in adding history of game--- $e")
            }
        }
    }

    fun deleteHistoryGame(historyGame: HistoryGame) {
        viewModelScope.launch {
            try {
                historyGameNetworkRepository.deleteHistoryGame(historyGame.id)
                historyGameDatabaseRepository.deleteHistoryGame(historyGame)
                removeHistoryGameFromList(historyGame)
            } catch (e: Exception) {
                Log.d ("GameHistory", "Error in deleting history of game--- $e")
            }
        }
    }

    private fun removeHistoryGameFromList(historyGame: HistoryGame) {
        historyGameList.toMutableList().also {
            it.remove(historyGame)
            historyGameList = it
        }
    }

    fun editHistoryGame (historyGame: HistoryGame) {
        viewModelScope.launch{
            try {
                addEditHistoryGameStatus = OperationStatus.LOADING
                historyGameNetworkRepository.editHistoryGame(historyGame)
                historyGameDatabaseRepository.editHistoryGame(historyGame)
                addEditHistoryGameStatus = OperationStatus.SUCCESS
            } catch (e: Exception) {
                addEditHistoryGameStatus = OperationStatus.ERROR
                Log.d ("GameHistory", "Error in edit history of game--- $e")
            }
        }
    }
}