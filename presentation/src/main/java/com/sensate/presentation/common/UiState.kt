package com.sensate.presentation.common

/**
 * Generic UI state class to handle different states of data loading.
 * @param T The type of data being loaded.
 */
sealed class UiState<out T> {
    /**
     * Loading state, indicating that data is being fetched.
     */
    data object Loading : UiState<Nothing>()
    
    /**
     * Success state, containing the loaded data.
     * @param data The successfully loaded data.
     */
    data class Success<T>(val data: T) : UiState<T>()
    
    /**
     * Error state, containing an error message.
     * @param message The error message.
     */
    data class Error(val message: String) : UiState<Nothing>()
} 