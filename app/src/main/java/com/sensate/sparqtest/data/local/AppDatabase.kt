package com.sensate.sparqtest.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sensate.sparqtest.data.local.dao.ItemDao
import com.sensate.sparqtest.data.local.entity.ItemEntity

/**
 * Room database for the application.
 */
@Database(
    entities = [ItemEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    /**
     * Gets the DAO for Item entities.
     * @return The ItemDao.
     */
    abstract fun itemDao(): ItemDao
} 