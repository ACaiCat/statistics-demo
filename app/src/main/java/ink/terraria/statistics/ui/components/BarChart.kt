package ink.terraria.statistics.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ink.terraria.statistics.data.Item
import ink.terraria.statistics.data.testData

private const val SCALE_FACTOR = 1.1f

@Composable
fun OutlineBarChart(
    data: List<Item>,
    modifier: Modifier = Modifier,
    height: Dp = 300.dp
) {
    Box(
        modifier = modifier
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = MaterialTheme.shapes.medium
            )
    ) {
        BarChart(data, height = height)
    }
}

@Composable
fun BarChart(
    data: List<Item>,
    modifier: Modifier = Modifier,
    height: Dp = 300.dp,
) {
    if (data.isEmpty()) return
    val maxValue = data.maxBy { it.value }.value
    val maxDrawValue = maxValue.coerceAtLeast(1.0) * SCALE_FACTOR

    LazyRow(
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .height(height)
            .fillMaxWidth()
    ) {
        items(data) { item ->
            Bar(
                tag = item.label,
                value = item.value,
                maxValue = maxDrawValue,
                modifier = Modifier.width(40.dp)
            )
        }
    }
}

@Composable
fun Bar(
    tag: String,
    value: Double,
    maxValue: Double,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()

    val animatedValue = remember { Animatable(0f) }

    LaunchedEffect(value, maxValue) {
        animatedValue.animateTo(
            targetValue = value.toFloat(),
            animationSpec = tween(
                durationMillis = 1000,
                delayMillis = 100,
                easing = FastOutSlowInEasing
            )
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier
    ) {
        Box(modifier = Modifier.weight(1f)) {
            val valueTextLayout = textMeasurer.measure(
                text = animatedValue.value.toInt().toString(),
                style = MaterialTheme.typography.labelMedium,
            )
            val shape = MaterialTheme.shapes.extraSmall
            val cornerRadius = with(LocalDensity.current) {
                (shape as RoundedCornerShape).topStart.toPx(Size.Unspecified, this)
            }

            val valueColor = MaterialTheme.colorScheme.onSurface
            val barColor = MaterialTheme.colorScheme.primary
            Canvas(modifier = Modifier.fillMaxSize()) {
                val barWidth = size.width
                // 这里负数有点不好处理，先统一算0
                val barHeight = (animatedValue.value.coerceAtLeast(0f) / maxValue * size.height).toFloat()
                val barTop = size.height - barHeight
                drawRoundRect(
                    color = barColor,
                    cornerRadius = CornerRadius(cornerRadius),
                    size = Size(barWidth, barHeight),
                    topLeft = Offset(
                        x = size.center.x - barWidth / 2f, y = barTop
                    )
                )

                drawText(
                    textLayoutResult = valueTextLayout,
                    topLeft = Offset(
                        x = size.center.x - valueTextLayout.size.width / 2f,
                        y = barTop - valueTextLayout.size.height - 4.dp.toPx()
                    ),
                    color = valueColor
                )
            }
        }

        Text(
            text = tag,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
@Preview(showBackground = true)
fun OutlineBarChartPreview() {
    OutlineBarChart(
        testData
    )
}

@Composable
@Preview(showBackground = true)
fun BarChartPreview() {
    BarChart(
        testData
    )
}
