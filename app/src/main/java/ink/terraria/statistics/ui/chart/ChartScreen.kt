package ink.terraria.statistics.ui.chart

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ink.terraria.statistics.R
import ink.terraria.statistics.data.Item
import ink.terraria.statistics.ui.AppViewModelProvider
import ink.terraria.statistics.ui.components.OutlineBarChart
import ink.terraria.statistics.ui.components.OutlineLineChart
import ink.terraria.statistics.ui.navigation.NavigationDestination
import ink.terraria.statistics.ui.theme.StatisticsTheme

object ChartDestination : NavigationDestination {
    override val route: String = "chart"
    override val titleRes: Int = R.string.chart
}

@Composable
fun ChartScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChartViewModel = viewModel(factory = AppViewModelProvider.factory)
) {
    val uiState by viewModel.chartUiState.collectAsState()

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            ChartBar(
                onBackClick = onBackClick
            )
        },
        modifier = modifier
    ) { paddingValues ->
        ChartBody(
            items = uiState.items,
            modifier = Modifier
                .padding(top = paddingValues.calculateTopPadding(), start = 16.dp, end = 16.dp)
                .fillMaxWidth()
        )
    }

}

@Composable
fun ChartBody(
    items: List<Item>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        OutlineLineChart(
            data = items, height = 300.dp, modifier = Modifier.padding(vertical = 16.dp)
        )
        OutlineBarChart(
            data = items, modifier = Modifier.padding(vertical = 16.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartBar(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier.padding(horizontal = 8.dp),
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
        },
        title = {
            Text(
                text = stringResource(R.string.chart),
                style = MaterialTheme.typography.titleLarge
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
fun ChartScreenPreview() {
    StatisticsTheme {
        ChartScreen(
            onBackClick = {}
        )
    }
}
