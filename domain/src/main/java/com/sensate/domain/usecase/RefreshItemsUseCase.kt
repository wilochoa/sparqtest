package com.sensate.domain.usecase

import com.sensate.domain.repository.ItemRepository
import javax.inject.Inject

/**
 * Use case to refresh items from the remote data source.
 * Follows the Single Responsibility Principle by having one specific task.
 */
class RefreshItemsUseCase @Inject constructor(
    private val repository: ItemRepository
) {
    /**
     * Execute the use case to refresh the items.
     * @return Result indicating success or failure of the refresh operation.
     */
    suspend operator fun invoke(): Result<Boolean> {
        return repository.refreshItems()
    }
} 