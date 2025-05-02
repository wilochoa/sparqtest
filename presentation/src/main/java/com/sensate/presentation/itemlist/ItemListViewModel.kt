package com.sensate.presentation.itemlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sensate.domain.model.Item
import com.sensate.domain.usecase.GetItemsUseCase
import com.sensate.domain.usecase.GetItemsWithRefreshUseCase
import com.sensate.domain.usecase.RefreshItemsUseCase
import com.sensate.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

/**
 * ViewModel for the item list screen.
 */
@HiltViewModel
class ItemListViewModel @Inject constructor(
    private val getItemsUseCase: GetItemsUseCase,
    private val getItemsWithRefreshUseCase: GetItemsWithRefreshUseCase,
    private val refreshItemsUseCase: RefreshItemsUseCase
) : ViewModel() {
    
    private val _itemsState = MutableStateFlow<UiState<List<Item>>>(UiState.Loading)
    
    /**
     * Observable UI state for the items list.
     */
    val itemsState: StateFlow<UiState<List<Item>>> = _itemsState
    
    /**
     * Flag to track if a refresh is in progress.
     */
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing
    
    /**
     * Message to show when there's a network error.
     */
    private val _networkMessage = MutableStateFlow<String?>(null)
    val networkMessage: StateFlow<String?> = _networkMessage
    
    init {
        loadItemsWithOfflineSupport()
    }
    
    /**
     * Loads items with offline-first approach.
     */
    private fun loadItemsWithOfflineSupport() {
        viewModelScope.launch {
            getItemsWithRefreshUseCase()
                .catch { e ->
                    handleError(e)
                }
                .collect { result ->
                    result.fold(
                        onSuccess = { items ->
                            _itemsState.value = UiState.Success(items)
                            if (items.isNotEmpty()) {
                                _networkMessage.value = null
                            }
                        },
                        onFailure = { e ->
                            handleError(e)
                        }
                    )
                }
        }
    }
    
    /**
     * Handle errors and update UI state appropriately.
     */
    private fun handleError(e: Throwable) {
        if (e is IOException) {
            // For network errors, show a message but don't change the UI state if we have data
            _networkMessage.value = e.message
            
            // Only change UI state to error if we don't have data
            if (_itemsState.value !is UiState.Success) {
                _itemsState.value = UiState.Error("No internet connection. Please connect to the internet and try again.")
            }
        } else {
            // For other errors, update the UI state
            _itemsState.value = UiState.Error(e.message ?: "Unknown error occurred")
        }
    }
    
    /**
     * Refreshes items from the remote data source.
     */
    fun refreshItems() {
        viewModelScope.launch {
            _isRefreshing.value = true
            _networkMessage.value = null // Clear any previous network messages
            
            // Save current state to restore if refresh fails
            val previousState = _itemsState.value
            
            try {
                refreshItemsUseCase().fold(
                    onSuccess = { 
                        // Fetch items directly instead of restarting the flow
                        val items = getItemsUseCase().first()
                        _itemsState.value = UiState.Success(items)
                    },
                    onFailure = { e ->
                        // Update the network message with the error message
                        _networkMessage.value = e.message ?: "Unknown error occurred"
                        
                        handleError(e)
                        // If we're still in Loading state, restore previous state
                        if (_itemsState.value is UiState.Loading && previousState is UiState.Success) {
                            _itemsState.value = previousState
                        }
                    }
                )
            } catch (e: Exception) {
                // If any exception occurs during refresh, restore previous state
                _networkMessage.value = e.message ?: "Unknown error occurred"
                
                if (previousState is UiState.Success) {
                    _itemsState.value = previousState
                } else {
                    handleError(e)
                }
            } finally {
                _isRefreshing.value = false
            }
        }
    }
} 