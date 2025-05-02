package com.sensate.data.remote.api

import com.sensate.data.remote.model.ItemDto
import retrofit2.http.GET

/**
 * Retrofit API service for fetching items.
 */
interface ItemApiService {
    /**
     * Gets all items from the API.
     * @return A list of ItemDtos.
     */
    @GET("test.json")
    suspend fun getItems(): List<ItemDto>
} 