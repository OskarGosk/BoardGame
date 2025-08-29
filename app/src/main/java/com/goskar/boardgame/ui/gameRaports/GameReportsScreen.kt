package com.goskar.boardgame.ui.gameRaports

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.components.scaffold.BoardGameScaffold
import com.goskar.boardgame.ui.components.scaffold.topBar.TopBarViewModel
import com.goskar.boardgame.ui.gameRaports.charts.ColumnChartGamesPlay
import com.goskar.boardgame.ui.gameRaports.components.SelectMonthRow
import com.goskar.boardgame.ui.gameRaports.components.SelectYearRow
import com.goskar.boardgame.ui.theme.BoardGameTheme
import com.goskar.boardgame.ui.theme.SmoochBold18
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import ir.ehsannarmani.compose_charts.models.Bars
import org.koin.androidx.compose.koinViewModel

class GameReportsScreen : Screen {
    @Composable
    override fun Content() {

        val viewModel: GameReportsViewModel = koinViewModel()
        val chartData by viewModel.chartData.collectAsState()
        val rowChartData by viewModel.rowChartData.collectAsState()
        val state by viewModel.state.collectAsState()

        val topBarViewModel: TopBarViewModel = koinViewModel()
        val topBarState by topBarViewModel.state.collectAsState()

        BoardGameScaffold(
            titlePage = stringResource(R.string.reports),
            selectedScreen = null,
            topBarState = topBarState,
            uploadDataToFirebase = topBarViewModel::uploadDataToFirebase
        ) { paddingValues ->

            GameReportsContent(
                state = state,
                chartData = chartData,
                rowChartData = rowChartData,
                prepareChart = viewModel::prepareChart,
                update = viewModel::update,
                paddingValues = paddingValues
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameReportsContent(
    state: GameReportsState,
    chartData: List<Bars>,
    rowChartData: List<Bars>,
    prepareChart: () -> Unit = {},
    update: (GameReportsState) -> Unit = {},
    paddingValues: PaddingValues
) {

    val calendarState = rememberSheetState()

    CalendarDialog(
        state = calendarState,
        config = CalendarConfig(
            monthSelection = true,
            yearSelection = true,
        ),
        selection = CalendarSelection.Period { startDate, endDate ->
            Log.d("Oskar22", "startDate --- $startDate")
            Log.d("Oskar22", "endDate --- $endDate")
            update(
                state.copy(
                    startDate = startDate,
                    endDate = endDate
                )
            )

        })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .padding(paddingValues)
    ) {


        if (chartData.isNotEmpty()) {
            ColumnChartGamesPlay(chartData)
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 15.dp)
                    .height(150.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No game history for the selected date.\nPlease select another date",
                    style = SmoochBold18,
                    textAlign = TextAlign.Center)
            }
        }
//            ColumnChart(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(bottom = 15.dp)
//                    .height(150.dp),
//                data = chartData,
//                indicatorProperties = HorizontalIndicatorProperties(
//                    textStyle = Smooch16.copy(color = MaterialTheme.colorScheme.primaryContainer),
//                    contentBuilder = { indicator ->
//                        "%.0f".format(indicator) + ""
//                    },
//                ),
//                labelProperties = labelProperties,
//                labelHelperProperties = LabelHelperProperties(false),
//                barProperties = BarProperties(
//                    cornerRadius = Bars.Data.Radius.Rectangle(topRight = 6.dp, topLeft = 6.dp),
//                ),
//                animationSpec = spring(
//                    dampingRatio = Spring.DampingRatioMediumBouncy,
//                    stiffness = Spring.StiffnessLow
//                ),
//            )

        Spacer(modifier = Modifier.height(10.dp))

        Row {
            SelectMonthRow(
                state = state,
                update = update,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                prepareChart = prepareChart
            )

            Spacer(modifier = Modifier.width(10.dp))

            SelectYearRow(
                state = state,
                update = update,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                prepareChart = prepareChart
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                shape = RoundedCornerShape(10.dp),
                onClick = {
                    calendarState.show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Period",
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = SmoochBold18
                    )
                    Text(
                        text = "${state.startDate}",
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = SmoochBold18
                    )
                    Text(
                        text = "${state.endDate}",
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = SmoochBold18
                    )
                }
            }
        }
    }


    Text(text = "Wykres słupkowy z liczbą gier")
    Text(text = "Do wyboru - rok kalendarzowy, miesiąc")


//            RowChart(
//                modifier = Modifier.fillMaxWidth()
//                    .padding(horizontal = 22.dp)
//                    .height(150.dp),
//                data = rowChartData,
//                barProperties = BarProperties(
//                    cornerRadius = Bars.Data.Radius.Rectangle(topRight = 6.dp, topLeft = 6.dp),
//                    spacing = 3.dp,
//                ),
//                animationSpec = spring(
//                    dampingRatio = Spring.DampingRatioMediumBouncy,
//                    stiffness = Spring.StiffnessLow
//                ),
//            )

}


@Preview
@Composable
fun GameReportsContentPreview() {

    val chartData = listOf(
        Bars(
            label = "Jan",
            values = listOf(
                Bars.Data(label = "Linux", value = 50.0, color = SolidColor(Color.Blue)),
                Bars.Data(label = "Windows", value = 70.0, color = SolidColor(Color.Red))
            ),
        ),
        Bars(
            label = "Feb",
            values = listOf(
                Bars.Data(label = "Linux", value = 50.0, color = SolidColor(Color.Blue)),
                Bars.Data(label = "Windows", value = 60.0, color = SolidColor(Color.Red))
            ),
        )
    )
    BoardGameScaffold(
        titlePage = stringResource(R.string.reports),
        selectedScreen = null
    ) { paddingValues ->

        GameReportsContent(GameReportsState(), chartData, chartData, paddingValues = paddingValues)
    }
}

@Preview
@Composable
fun GameReportsContentEmptyPreview() {

    val chartData : List<Bars> = emptyList()
    BoardGameScaffold(
        titlePage = stringResource(R.string.reports),
        selectedScreen = null
    ) { paddingValues ->

        GameReportsContent(GameReportsState(), chartData, chartData, paddingValues = paddingValues)
    }
}

@Preview
@Composable
fun ColorPreview() {
    BoardGameTheme {
        val color = MaterialTheme.colorScheme.primary
        Row {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(color)
            )

            Button(onClick = {},
                modifier = Modifier
                    .size(100.dp),
                shape = RoundedCornerShape(0)
            ) { }
        }
    }
}