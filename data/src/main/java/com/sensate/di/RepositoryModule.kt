package com.sensate.di

import com.sensate.data.repository.ItemRepositoryImpl
import com.sensate.domain.repository.ItemRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for binding repository implementations.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    /**
     * Binds the ItemRepositoryImpl to the ItemRepository interface.
     * @param repository The implementation instance.
     * @return The repository instance as the interface type.
     */
    @Binds
    @Singleton
    abstract fun bindItemRepository(
        repository: ItemRepositoryImpl
    ): ItemRepository
} 