package com.sensate.sparqtest.data.remote.model

import com.sensate.sparqtest.domain.model.Item
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.UUID

/**
 * Data Transfer Object for Item data from the API.
 */
@JsonClass(generateAdapter = true)
data class ItemDto(
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String
) {
    /**
     * Maps the DTO to a domain model.
     * @return A domain model Item with a generated ID.
     */
    fun toDomainModel(): Item {
        return Item(
            id = UUID.randomUUID().toString(),
            title = title,
            description = description
        )
    }
} 