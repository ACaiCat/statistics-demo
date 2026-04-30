package ink.terraria.statistics.data

import android.content.Context

interface AppContainer {
    val itemRepository: ItemRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val itemRepository: ItemRepository by lazy {
        ItemRepository(ItemDatabase.getDatabase(context).itemDao())
    }
}
