package com.sensate.domain.repository

import com.sensate.domain.model.Item
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for accessing Item data.
 * Follows the Repository pattern to abstract the data sources from the domain layer.
 */
interface ItemRepository {
    /**
     * Gets all items as a Flow.
     * @return A Flow emitting a list of Items from local database.
     */
    fun getItems(): Flow<List<Item>>
    
    /**
     * Gets a specific item by its ID.
     * @param id The ID of the item to retrieve.
     * @return A Flow emitting the requested Item or null if not found.
     */
    fun getItemById(id: String): Flow<Item?>
    
    /**
     * Refreshes the data from the remote source.
     * @return Result indicating success or failure of the refresh operation.
     */
    suspend fun refreshItems(): Result<Boolean>
    
    /**
     * Checks if there's any cached data in the local database.
     * @return true if local data exists, false otherwise.
     */
    suspend fun hasLocalData(): Boolean
    
    /**
     * Gets items with smart refresh handling for offline-first approach.
     * - If there's local data, it returns that immediately while attempting to refresh.
     * - If there's no local data, it tries to fetch from remote and returns the result.
     * @return A Flow emitting Result objects containing either the list of items or an error.
     */
    fun getItemsWithRefresh(): Flow<Result<List<Item>>>
} 