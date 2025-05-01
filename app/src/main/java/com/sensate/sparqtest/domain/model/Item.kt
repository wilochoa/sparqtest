package com.sensate.sparqtest.domain.model

/**
 * Domain model representing an item with a title and description.
 */
data class Item(
    val id: String,
    val title: String,
    val description: String
) 