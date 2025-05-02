package com.sensate.di

import android.content.Context
import androidx.room.Room
import com.sensate.data.local.AppDatabase
import com.sensate.data.local.dao.ItemDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing database-related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    /**
     * Provides the Room database instance.
     * @param context The application context.
     * @return An AppDatabase instance.
     */
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "sparq_test_database"
        ).build()
    }
    
    /**
     * Provides the ItemDao for database operations.
     * @param database The AppDatabase instance.
     * @return An ItemDao instance.
     */
    @Provides
    fun provideItemDao(database: AppDatabase): ItemDao {
        return database.itemDao()
    }
} 