package ink.terraria.statistics.data

import kotlinx.coroutines.flow.Flow

class ItemRepository(private val itemDao: ItemDao) {
    suspend fun insertItem(item: Item): Long = itemDao.insert(item)
    suspend fun deleteItem(item: Item) = itemDao.delete(item)
    fun getAllItemsStream(): Flow<List<Item>> = itemDao.getAllItems()
}
