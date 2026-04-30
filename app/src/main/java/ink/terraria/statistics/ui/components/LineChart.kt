package ink.terraria.statistics.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ink.terraria.statistics.data.Item
import ink.terraria.statistics.data.testData
import kotlin.math.sqrt


@Composable
fun OutlineLineChart(
    data: List<Item>, modifier: Modifier = Modifier, height: Dp = 300.dp
) {
    Box(
        modifier = modifier.border(
            width = 2.dp,
            color = MaterialTheme.colorScheme.outlineVariant,
            shape = MaterialTheme.shapes.medium
        )
    ) {
        LineChart(data, height = height)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LineChart(
    data: List<Item>, modifier: Modifier = Modifier, height: Dp = 300.dp
) {
    if (data.isEmpty()) return
    val maxValue = data.maxBy { it.value }.value
    val maxDrawValue = maxValue.coerceAtLeast(1.0) * 1.1

    LazyRow(
        contentPadding = PaddingValues(16.dp),
        modifier = modifier
            .height(height)
            .fillMaxWidth(),

        ) {
        itemsIndexed(data) { index, _ ->
            ChartColumn(index, data, maxDrawValue)
        }
    }

}

@Composable
fun ChartColumn(
    index: Int,
    data: List<Item>,
    maxDrawValue: Double,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val point = data[index]
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Box(
            modifier = Modifier
                .weight(1f)
                .width(60.dp)
        ) {
            val nextPoint = data.getOrNull(index + 1)
            val prevPoint = data.getOrNull(index - 1)
            val prevValueTextLayout = textMeasurer.measure(
                text = prevPoint?.value.toString(),
                style = MaterialTheme.typography.labelMedium,
            )
            val nextValueTextLayout = textMeasurer.measure(
                text = nextPoint?.value.toString(),
                style = MaterialTheme.typography.labelMedium,
            )
            val valueColor = MaterialTheme.colorScheme.onSurface
            val lineColor = MaterialTheme.colorScheme.outlineVariant
            val pointColor = MaterialTheme.colorScheme.primary

            Canvas(Modifier.fillMaxSize()) {
                val centerX = size.width / 2
                val radius = 4.dp.toPx()
                val y: Float = calcY(point.value, maxDrawValue, size.height).toFloat()

                if (nextPoint != null) {
                    val yNext: Float = calcY(nextPoint.value, maxDrawValue, size.height).toFloat()

                    drawSegment(
                        fromX = centerX,
                        toX = centerX + size.width,
                        fromY = y,
                        toY = yNext,
                        radius = radius,
                        lineColor = lineColor
                    )

                    drawPointWithLabel(
                        x = centerX + size.width,
                        y = yNext,
                        radius = radius,
                        pointColor = pointColor,
                        textLayout = nextValueTextLayout,
                        valueColor = valueColor
                    )
                }


                if (prevPoint != null) {
                    val yPrev: Float = calcY(prevPoint.value, maxDrawValue, size.height).toFloat()

                    drawSegment(
                        fromX = -size.width / 2,
                        toX = centerX,
                        fromY = yPrev,
                        toY = y,
                        radius = radius,
                        lineColor = lineColor
                    )

                    drawPointWithLabel(
                        x = -size.width / 2,
                        y = yPrev,
                        radius = radius,
                        pointColor = pointColor,
                        textLayout = prevValueTextLayout,
                        valueColor = valueColor
                    )
                }
            }
        }

        Text(
            text = point.label,
            maxLines = 1,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

private fun calcY(value: Double, maxValue: Double, height: Float): Double {
    return (height - value / maxValue * height)
}

private fun DrawScope.drawSegment(
    fromX: Float, toX: Float, fromY: Float, toY: Float, radius: Float, lineColor: Color
) {
    val dx = toX - fromX
    val dy = toY - fromY
    val len = sqrt(dx * dx + dy * dy)
    if (len == 0f) return
    val ux = dx / len
    val uy = dy / len

    drawLine(
        color = lineColor,
        strokeWidth = Stroke.DefaultMiter,
        start = Offset((fromX + ux * radius), (fromY + uy * radius)),
        end = Offset((toX - ux * radius), (toY - uy * radius))
    )
}

private fun DrawScope.drawPointWithLabel(
    x: Float,
    y: Float,
    radius: Float,
    pointColor: Color,
    textLayout: TextLayoutResult,
    valueColor: Color
) {
    drawCircle(
        color = pointColor, center = Offset(x, y), radius = radius
    )
    drawText(
        textLayoutResult = textLayout, topLeft = Offset(
            x = (x - textLayout.size.width / 2f), y = (y - 20.dp.toPx())
        ), color = valueColor
    )
}

@Composable
@Preview(showBackground = true)
fun LineChartPreview() {
    LineChart(testData)
}
