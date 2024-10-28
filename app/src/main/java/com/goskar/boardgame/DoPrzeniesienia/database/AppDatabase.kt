package OGosk.boardgamebase.database

import OGosk.boardgamebase.model.Game
import OGosk.boardgamebase.model.HistoryGame
import OGosk.boardgamebase.model.Player
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

//@Database(entities = [Player::class], version = 2, exportSchema = false)
//abstract class AppDatabase: RoomDatabase(){
//    abstract fun playerDao(): PlayerDao
//}
//
//@Database(entities = [Game::class], version = 2, exportSchema = false)
//abstract class AppDatabaseGame: RoomDatabase(){
//    abstract fun gameDao(): GameDao
//}

@Database(entities = [Player::class, Game::class, HistoryGame::class], version = 5, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
    abstract fun gameDao(): GameDao
    abstract fun historyGameDao(): HistoryGameDao
}