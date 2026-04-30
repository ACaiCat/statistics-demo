package ink.terraria.statistics.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ink.terraria.statistics.data.Item
import ink.terraria.statistics.data.ItemRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(private val itemRepository: ItemRepository) : ViewModel() {
    private val _allItems: StateFlow<List<Item>> = itemRepository.getAllItemsStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = listOf()
        )

    val homeUiState: StateFlow<HomeUiState> = _allItems.map {
        HomeUiState(items = it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = HomeUiState()
    )

    fun deleteItem(item: Item) {
        viewModelScope.launch {
            itemRepository.deleteItem(item)
        }
    }

    fun insertItem(item: Item) {
        viewModelScope.launch {
            itemRepository.insertItem(item)
        }
    }
}

data class HomeUiState(
    val items: List<Item> = listOf(),
)
