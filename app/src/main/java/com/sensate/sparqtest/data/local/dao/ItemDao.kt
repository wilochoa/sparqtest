package com.sensate.sparqtest.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sensate.sparqtest.data.local.entity.ItemEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the items table.
 */
@Dao
interface ItemDao {
    /**
     * Gets all items from the database.
     * @return A Flow emitting a list of all ItemEntities.
     */
    @Query("SELECT * FROM items")
    fun getAll(): Flow<List<ItemEntity>>
    
    /**
     * Gets a specific item by its ID.
     * @param id The ID of the item to retrieve.
     * @return A Flow emitting the requested ItemEntity or null if not found.
     */
    @Query("SELECT * FROM items WHERE id = :id")
    fun getById(id: String): Flow<ItemEntity?>
    
    /**
     * Inserts or replaces items in the database.
     * @param items The list of ItemEntities to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ItemEntity>)
    
    /**
     * Deletes all items from the database.
     */
    @Query("DELETE FROM items")
    suspend fun deleteAll()
    
    /**
     * Replaces all items in the database in a single transaction.
     * @param items The new list of ItemEntities.
     */
    @androidx.room.Transaction
    suspend fun replaceAll(items: List<ItemEntity>) {
        deleteAll()
        insertAll(items)
    }
} 