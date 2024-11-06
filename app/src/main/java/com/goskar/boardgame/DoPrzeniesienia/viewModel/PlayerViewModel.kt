package OGosk.boardgamebase.viewModel

//import OGosk.boardgamebase.api.PlayerNetworkRepository
//import OGosk.boardgamebase.database.PlayerDatabaseRepository
//import OGosk.boardgamebase.model.Player
//import OGosk.boardgamebase.model.OperationStatus
//import OGosk.boardgamebase.model.PlayerIdRespons
//import android.util.Log
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.setValue
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import kotlinx.coroutines.launch
//
//class PlayerViewModel (
//    private var playerDatabaseRepository: PlayerDatabaseRepository,
//    private var playerNetworkRepository: PlayerNetworkRepository
//): ViewModel() {
//    var playerList by mutableStateOf(emptyList<Player>())
//    var getAllPlayerStatus by mutableStateOf(OperationStatus.UNKNOWN)
//    var addEditPlayerStatus by mutableStateOf(OperationStatus.UNKNOWN)
//
//    fun getAllPlayer () {
//        viewModelScope.launch {
//            try {
//                getAllPlayerStatus = OperationStatus.LOADING
//                playerList = playerNetworkRepository.getAllPlayer().toMutableList()
//                playerDatabaseRepository.insertAllPlayer(playerList)
////                playerList = playerDatabaseRepository.getAllPlayers().toMutableList()
//                getAllPlayerStatus = OperationStatus.SUCCESS
//            } catch (e: Exception){
//                getAllPlayerStatus = OperationStatus.ERROR
//                Log.d ("Game", "Error in loading all player --- $e")
//            }
//        }
//    }
//
//    fun addPlayer(player: Player) {
//        viewModelScope.launch {
//            try {
//                getAllPlayerStatus = OperationStatus.LOADING
//                val response: PlayerIdRespons =playerNetworkRepository.addPlayer(player)
//                playerDatabaseRepository.insertPlayer(player.copy(id = response.name))
//                getAllPlayerStatus = OperationStatus.SUCCESS
//            } catch (e: Exception) {
//                getAllPlayerStatus = OperationStatus.ERROR
//                Log.d ("Game", "Nie dodało gracza --- $e")
//            }
//        }
//    }
//
//    fun deletePlayer(player: Player) {
//        viewModelScope.launch {
//            try {
//                playerNetworkRepository.deletePlayer(player.id)
//                playerDatabaseRepository.deletePlayer(player)
//                removePlayerFromList(player)
//            } catch (e: Exception) {
//                Log.d ("Game", "Nie usuneło gracza --- $e")
//            }
//        }
//    }
//
//    private fun removePlayerFromList (player: Player) {
//        playerList.toMutableList().also {
//            it.remove(player)
//            playerList = it
//        }
//    }
//
//    fun editPlayer (player: Player) {
//        viewModelScope.launch {
//            try {
//                addEditPlayerStatus = OperationStatus.LOADING
//                playerNetworkRepository.editPlayer(player)
//                playerDatabaseRepository.editPlayer(player)
//                addEditPlayerStatus = OperationStatus.SUCCESS
//            } catch (e: Exception) {
//                addEditPlayerStatus = OperationStatus.ERROR
//                Log.d ("Game", "Nie edytowało gracza --- $e")
//            }
//        }
//    }
//
//    fun selectedPlayer (player: Player) {
//        player.selected = !player.selected
//        Log.d("Oskar","${player.selected}")
//    }
//
//}