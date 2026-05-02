package ink.terraria.statistics.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ink.terraria.statistics.data.Item
import ink.terraria.statistics.data.testData
import kotlin.math.sqrt

private const val SCALE_FACTOR = 1.1f

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
        LineChart(data = data, height = height)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LineChart(
    data: List<Item>, modifier: Modifier = Modifier, height: Dp = 300.dp
) {
    if (data.isEmpty()) return

    val animatedProcess = remember { Animatable(0f) }

    LaunchedEffect(data) {
        animatedProcess.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 1000,
                delayMillis = 100,
                easing = FastOutSlowInEasing
            )
        )
    }
    LazyRow(
        contentPadding = PaddingValues(16.dp),
        modifier = modifier
            .height(height)
            .fillMaxWidth(),
    ) {
        itemsIndexed(data) { index, _ ->
            ChartColumn(index, data, animatedProcess.value)
        }
    }
}

@Composable
fun ChartColumn(
    index: Int,
    data: List<Item>,
    animatedProcess: Float,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val point = data[index]
    val maxValue = data.maxBy { it.value }.value
    val minValue = data.minBy { it.value }.value

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.width(60.dp)
    ) {
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
                // 这里取Box组件的X中心点作为折线图的小圆点的X坐标
                val centerX = size.width / 2
                val radius = 4.dp.toPx()
                val y: Float =
                    calcY(point.value, minValue, maxValue, size.height, animatedProcess).toFloat()

                // 绘制下一个点和与之相连的线
                if (nextPoint != null) {
                    val yNext: Float =
                        calcY(
                            nextPoint.value,
                            minValue,
                            maxValue,
                            size.height,
                            animatedProcess
                        ).toFloat()

                    drawSegment(
                        fromX = centerX,
                        toX = centerX + size.width,
                        fromY = y,
                        toY = yNext,
                        radius = radius,
                        lineColor = lineColor
                    )

                    drawPointWithValue(
                        x = centerX + size.width,
                        y = yNext,
                        radius = radius,
                        pointColor = pointColor,
                        textLayout = nextValueTextLayout,
                        valueColor = valueColor
                    )
                }

                // 这里会重复绘制是有意为之的，原来设计是只绘制下一个点和与之相连的线
                // 但是发现LazyColumn会提前卸载上一条线，然后向右滑动的时候边缘的线会提前消失
                // 而且LazyColumn好像不让改预加载了
                // 所以这里还绘制了制前一个点和与之相连的线来防止前面边缘的线提前消失

                // 绘制前一个点和与之相连的线
                if (prevPoint != null) {
                    val yPrev: Float =
                        calcY(
                            prevPoint.value,
                            minValue,
                            maxValue,
                            size.height,
                            animatedProcess
                        ).toFloat()

                    drawSegment(
                        fromX = -size.width / 2,
                        toX = centerX,
                        fromY = yPrev,
                        toY = y,
                        radius = radius,
                        lineColor = lineColor
                    )

                    drawPointWithValue(
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
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .padding(top = 4.dp)
        )
    }
}

private fun calcY(
    value: Double,
    minValue: Double,
    maxValue: Double,
    height: Float,
    animatedProcess: Float
): Double {
    val relativeValue = value - minValue
    val relativeMaxValue = maxValue - minValue

    val proportion = relativeValue / (relativeMaxValue * SCALE_FACTOR)

    return (height - proportion * animatedProcess * height)
}

private fun DrawScope.drawSegment(
    fromX: Float, toX: Float, fromY: Float, toY: Float, radius: Float, lineColor: Color
) {
    // 下面的计算是为了避开折现图的小圆点
    val dx = toX - fromX
    val dy = toY - fromY
    val len = sqrt(dx * dx + dy * dy)

    // 防止除0，但是按理说也不会有0，算防御性编程了
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

private fun DrawScope.drawPointWithValue(
    x: Float,
    y: Float,
    radius: Float,
    pointColor: Color,
    textLayout: TextLayoutResult,
    valueColor: Color
) {
    drawCircle(
        color = pointColor,
        center = Offset(x, y),
        radius = radius
    )
    drawText(
        textLayoutResult = textLayout,
        topLeft = Offset(
            x = (x - textLayout.size.width / 2f),
            // 这里补了一个半径，不然小圆点和值有点挤
            y = (y - textLayout.size.height - radius - 4.dp.toPx())
        ),
        color = valueColor
    )
}

@Composable
@Preview(showBackground = true)
fun LineChartPreview() {
    LineChart(testData)
}
