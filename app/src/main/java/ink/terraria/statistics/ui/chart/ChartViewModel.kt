package ink.terraria.statistics.ui.chart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ink.terraria.statistics.data.Item
import ink.terraria.statistics.data.ItemRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ChartViewModel(itemRepository: ItemRepository) : ViewModel() {
    private val _allItems: StateFlow<List<Item>> = itemRepository.getAllItemsStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = listOf()
        )

    val chartUiState: StateFlow<ChartUiState> = _allItems.map {
        ChartUiState(items = it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = ChartUiState()
    )
}

data class ChartUiState(
    val items: List<Item> = listOf(),
)
