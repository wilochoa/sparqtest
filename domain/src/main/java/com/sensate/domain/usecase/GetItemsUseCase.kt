package com.sensate.domain.usecase

import com.sensate.domain.model.Item
import com.sensate.domain.repository.ItemRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to get all items.
 * Follows the Single Responsibility Principle by having one specific task.
 */
class GetItemsUseCase @Inject constructor(
    private val repository: ItemRepository
) {
    /**
     * Execute the use case to get all items.
     * @return A Flow emitting a list of Items.
     */
    operator fun invoke(): Flow<List<Item>> {
        return repository.getItems()
    }
} 