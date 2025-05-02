package com.sensate.presentation.itemdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sensate.domain.model.Item
import com.sensate.domain.usecase.GetItemByIdUseCase
import com.sensate.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the item detail screen.
 */
@HiltViewModel
class ItemDetailViewModel @Inject constructor(
    private val getItemByIdUseCase: GetItemByIdUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val _itemState = MutableStateFlow<UiState<Item>>(UiState.Loading)
    
    /**
     * Observable UI state for the item detail.
     */
    val itemState: StateFlow<UiState<Item>> = _itemState
    
    init {
        // Get the item ID from the saved state handle (navigation arguments)
        savedStateHandle.get<String>("itemId")?.let { itemId ->
            loadItem(itemId)
        } ?: run {
            _itemState.value = UiState.Error("No item ID provided")
        }
    }
    
    /**
     * Loads a specific item by ID.
     * @param id The ID of the item to load.
     */
    private fun loadItem(id: String) {
        viewModelScope.launch {
            getItemByIdUseCase(id)
                .catch { e ->
                    _itemState.value = UiState.Error(e.message ?: "Unknown error")
                }
                .collect { item ->
                    if (item != null) {
                        _itemState.value = UiState.Success(item)
                    } else {
                        _itemState.value = UiState.Error("Item not found")
                    }
                }
        }
    }
} 