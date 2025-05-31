package com.goskar.boardgame.ui.gameRaports.charts

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goskar.boardgame.ui.gameRaports.GameReportsContent
import com.goskar.boardgame.ui.theme.Smooch16
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties

@Composable
fun ColumnChartGamesPlay(
    chartData: List<Bars>
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
}


@Preview (showBackground = true)
@Composable
fun ColumnChartGamesPlayPreview() {

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
    ColumnChartGamesPlay(chartData)
}