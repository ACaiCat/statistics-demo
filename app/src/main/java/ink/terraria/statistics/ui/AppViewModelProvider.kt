package ink.terraria.statistics.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import ink.terraria.statistics.StatisticsApplication
import ink.terraria.statistics.ui.chart.ChartViewModel
import ink.terraria.statistics.ui.home.HomeViewModel

object AppViewModelProvider {
    val factory = viewModelFactory {
        initializer {
            HomeViewModel(StatisticsApplication().appContainer.itemRepository)
        }

        initializer {
            ChartViewModel(StatisticsApplication().appContainer.itemRepository)
        }
    }
}

fun CreationExtras.StatisticsApplication(): StatisticsApplication {
    return this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as StatisticsApplication
}
