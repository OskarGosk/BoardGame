package com.goskar.boardgame.ui.gameRaports

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goskar.boardgame.data.models.Game
import com.goskar.boardgame.data.models.HistoryGame
import com.goskar.boardgame.data.repository.dbRepository.GamesHistoryDbRepository
import com.goskar.boardgame.data.rest.RequestResult
import com.goskar.boardgame.data.useCase.GetAllGameUseCase
import com.goskar.boardgame.ui.theme.secondaryLight
import com.goskar.boardgame.utils.Months
import ir.ehsannarmani.compose_charts.models.Bars
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class GameReportsState(
    val minYear: Int = 2025,
    val maxYear: Int = 2025
)

class GameReportsViewModel(
    private val historyDbRepository: GamesHistoryDbRepository,
    private val allGameUseCase: GetAllGameUseCase
):ViewModel() {

    private val _state = MutableStateFlow(GameReportsState())
    val state = _state.asStateFlow()

    private val _gameHistory = MutableStateFlow<List<HistoryGame>>(emptyList())
    val gameHistory = _gameHistory.asStateFlow()

    private val _allGame = MutableStateFlow<List<Game>>(emptyList())
    val allGame = _allGame.asStateFlow()

    private val _chartData = MutableStateFlow<List<Bars>>(emptyList())
    val chartData = _chartData.asStateFlow()

    private val _rowChartData = MutableStateFlow<List<Bars>>(emptyList())
    val rowChartData = _rowChartData.asStateFlow()

    init {
        viewModelScope.launch {
            _allGame.value = allGameUseCase.invoke()
            playGamesAllTimeData()

            when(val response = historyDbRepository.getAllHistoryGame()) {
                is RequestResult.Success -> {
                    _gameHistory.value = response.data

                    if(response.data.isNotEmpty()) {
                        _state.update {
                            it.copy(
                                maxYear = (_gameHistory.value.map { it.gameData.year }).max(),
                                minYear = (_gameHistory.value.map { it.gameData.year }).min()
                            )
                        }
                        yearPlaysTimeData()
                    }


                }
                else -> {

                }
            }

        }
    }

    fun update(state: GameReportsState) {
        _state.update { state }
    }

    fun monthlyPlaysTimeData(year:Int) {
        val monthlyPlaysTimeData: MutableList<Bars> = emptyList<Bars>().toMutableList()
        Months.entries.forEach{ months ->
            val countRecord = _gameHistory.value.filter { it.gameData.monthValue == months.monthsNumber && it.gameData.year == year}
            val bars = Bars(
                label = months.monthsName,
                values = listOf(
                    Bars.Data(
                        value =  countRecord.size.toDouble(),
                        color = SolidColor(secondaryLight)
                    )
                )
            )

            monthlyPlaysTimeData += bars
        }

        _chartData.value = monthlyPlaysTimeData
    }

    fun yearPlaysTimeData() {
        val yearPlaysTimeData: MutableList<Bars> = emptyList<Bars>().toMutableList()

        (state.value.minYear..state.value.maxYear).forEach { year ->
            val countRecord = _gameHistory.value.filter { it.gameData.year == year }
            if (countRecord.isNotEmpty()) {
                val bars = Bars(
                    label = year.toString(),
                    values = listOf(
                        Bars.Data(
                            value = countRecord.size.toDouble(),
                            color = SolidColor(secondaryLight)
                        )
                    )
                )
                yearPlaysTimeData += bars
            }
        }
        _chartData.value = yearPlaysTimeData
    }

    fun playGamesAllTimeData() {
        val playGamesAllTimeData: MutableList<Bars> = emptyList<Bars>().toMutableList()
        val allGamesCount: Int = _allGame.value.sumOf { it.games }

        _allGame.value.forEach {
            playGamesAllTimeData += Bars(
                label = it.name,
                values = listOf(
                    Bars.Data(value = (it.games.toDouble()/allGamesCount), color = SolidColor(Color.Blue))
                )
            )
        }

        _rowChartData.value = playGamesAllTimeData
    }
}

