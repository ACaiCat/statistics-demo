package ink.terraria.statistics.ui.home

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ink.terraria.statistics.R
import ink.terraria.statistics.data.Item
import ink.terraria.statistics.ui.AppViewModelProvider
import ink.terraria.statistics.ui.components.AddItemDialog
import ink.terraria.statistics.ui.components.StatisticsItem
import ink.terraria.statistics.ui.navigation.NavigationDestination
import ink.terraria.statistics.ui.theme.StatisticsTheme

object HomeDestination : NavigationDestination {
    override val route: String = "home"
    override val titleRes: Int = R.string.app_name
}

@Composable
fun HomeScreen(
    onDrawChartClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.factory)
) {
    val uiState by viewModel.homeUiState.collectAsState()
    val context = LocalContext.current
    val showDialog = remember { mutableStateOf(false) }

    if (showDialog.value) {
        AddItemDialog(
            onDismiss = {
                showDialog.value = false
            },
            onConfirm = { label, value ->
                viewModel.insertItem(
                    Item(
                        id = 0,
                        label = label,
                        value = value,
                    )
                )
                showDialog.value = false
            }
        )
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            HomeBar()
        },
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(bottom = 42.dp)
            ) {
                ExtendedFloatingActionButton(
                    onClick = {
                        showDialog.value = true
                    },
                ) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_item))
                    Text(
                        text = stringResource(R.string.add_item),
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                ExtendedFloatingActionButton(
                    onClick = {
                        if (uiState.items.isEmpty()) {
                            Toast.makeText(context, R.string.no_any_item, Toast.LENGTH_SHORT).show()
                        } else {
                            onDrawChartClick()
                        }
                    }
                ) {
                    Icon(
                        Icons.Default.BarChart,
                        contentDescription = stringResource(R.string.draw_chart)
                    )
                    Text(
                        text = stringResource(R.string.draw_chart),
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

        },
        floatingActionButtonPosition = FabPosition.End,
        modifier = modifier
    ) { paddingValues ->
        HomeBody(
            items = uiState.items,
            onItemRemove = { item ->
                viewModel.deleteItem(item)
            },
            modifier = Modifier
                .padding(top = paddingValues.calculateTopPadding(), start = 16.dp, end = 16.dp)
                .fillMaxWidth()
        )
    }
}

@Composable
fun HomeBody(
    items: List<Item>,
    onItemRemove: (Item) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = items,
            key = { it.id }
        ) { item ->
            AnimatedVisibility(
                visible = true,
                modifier = Modifier.animateItem(),
                exit = shrinkVertically(tween(300)) + fadeOut(tween(200))
            ) {
                StatisticsItem(
                    item = item,
                    onRemove = onItemRemove
                )
            }
        }
    }
    Spacer(Modifier.padding(vertical = 16.dp))
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeBar(
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier.padding(horizontal = 8.dp),
        title = {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleLarge
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    StatisticsTheme {
        HomeScreen(onDrawChartClick = {})
    }
}


