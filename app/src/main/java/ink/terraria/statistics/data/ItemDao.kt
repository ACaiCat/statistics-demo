package ink.terraria.statistics.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Item): Long

    @Delete
    suspend fun delete(item: Item)

    @Query("SELECT * FROM items ORDER BY id ASC")
    fun getAllItems(): Flow<List<Item>>

}
