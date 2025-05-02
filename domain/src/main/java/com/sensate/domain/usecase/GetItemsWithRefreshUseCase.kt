package com.sensate.domain.usecase

import com.sensate.domain.model.Item
import com.sensate.domain.repository.ItemRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to get all items with smart offline-first handling.
 * This use case is specifically designed for handling offline scenarios:
 * - If there's local data, it returns that immediately while attempting to refresh.
 * - If there's no local data, it tries to fetch from remote and returns the result.
 */
class GetItemsWithRefreshUseCase @Inject constructor(
    private val repository: ItemRepository
) {
    /**
     * Execute the use case to get all items with offline-first approach.
     * @return A Flow emitting Result objects containing either the list of items or an error.
     */
    operator fun invoke(): Flow<Result<List<Item>>> {
        return repository.getItemsWithRefresh()
    }
} 