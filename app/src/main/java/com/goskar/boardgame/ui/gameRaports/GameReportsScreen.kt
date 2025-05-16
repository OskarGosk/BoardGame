package com.goskar.boardgame.ui.gameRaports

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.goskar.boardgame.R
import com.goskar.boardgame.ui.components.scaffold.BoardGameScaffold
import com.goskar.boardgame.ui.theme.Smooch16
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import org.koin.androidx.compose.koinViewModel

class GameReportsScreen : Screen {
    @Composable
    override fun Content() {

        val viewModel: GameReportsViewModel = koinViewModel()
        val chartData by viewModel.chartData.collectAsState()

        GameReportsContent(
            chartData = chartData,
            prepareMonthlyChart = viewModel::monthlyPlaysTimeData,
            prepareYearChart = viewModel::yearPlaysTimeData
        )
    }
}

@Composable
fun GameReportsContent(
    chartData: List<Bars>,
    prepareMonthlyChart: (Int) -> Unit = {},
    prepareYearChart: () -> Unit = {}
) {
    BoardGameScaffold(
        titlePage = stringResource(R.string.reports),
        selectedScreen = null
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
                .padding(paddingValues)
        ) {
            val labelProperties = LabelProperties(
                enabled = true,
                textStyle = Smooch16.copy(color = MaterialTheme.colorScheme.primaryContainer),
                padding = 1.dp,
                rotation = LabelProperties.Rotation(
                    mode = LabelProperties.Rotation.Mode.Force,
                    degree = -45f
                )
            )
            ColumnChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 15.dp)
                    .height(150.dp),
                data = chartData,
                indicatorProperties = HorizontalIndicatorProperties(
                    textStyle = Smooch16.copy(color = MaterialTheme.colorScheme.primaryContainer),
                    contentBuilder = { indicator ->
                        "%.0f".format(indicator) + ""
                    },
                ),
                labelProperties = labelProperties,
                labelHelperProperties = LabelHelperProperties(false),
                barProperties = BarProperties(
                    cornerRadius = Bars.Data.Radius.Rectangle(topRight = 6.dp, topLeft = 6.dp),
                ),
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
            )

            Row {
                Button(onClick = {
                    prepareMonthlyChart(2025)
                }) {
                    Text("Monthly 2025")
                }

                Button(onClick = {
                    prepareYearChart()
                }) {
                    Text("Year")
                }
            }



            Text(text = "Wykres słupkowy z liczbą gier")
            Text(text = "Do wyboru - rok kalendarzowy, miesiąc, tydzień")

        }
    }
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
    GameReportsContent(chartData)
}

@Preview
@Composable
fun ColorPreview() {
    val color = MaterialTheme.colorScheme.primaryContainer
    Box(
        modifier = Modifier
            .size(100.dp)
            .background(color)
    )
}