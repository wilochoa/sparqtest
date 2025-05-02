package com.sensate.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sensate.domain.model.Item

/**
 * Database entity representing an Item.
 */
@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String
) {
    /**
     * Maps the entity to a domain model.
     * @return A domain model Item.
     */
    fun toDomainModel(): Item {
        return Item(
            id = id,
            title = title,
            description = description
        )
    }
    
    companion object {
        /**
         * Maps a domain model to an entity.
         * @param item The domain model to convert.
         * @return An ItemEntity.
         */
        fun fromDomainModel(item: Item): ItemEntity {
            return ItemEntity(
                id = item.id,
                title = item.title,
                description = item.description
            )
        }
    }
} 