package com.sensate.sparqtest.domain.usecase

import com.sensate.sparqtest.domain.model.Item
import com.sensate.sparqtest.domain.repository.ItemRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to get a specific item by ID.
 * Follows the Single Responsibility Principle by having one specific task.
 */
class GetItemByIdUseCase @Inject constructor(
    private val repository: ItemRepository
) {
    /**
     * Execute the use case to get a specific item by ID.
     * @param id The ID of the item to retrieve.
     * @return A Flow emitting the requested Item or null if not found.
     */
    operator fun invoke(id: String): Flow<Item?> {
        return repository.getItemById(id)
    }
} 