package com.sensate.sparqtest.presentation.itemlist

import app.cash.turbine.test
import com.sensate.sparqtest.domain.model.Item
import com.sensate.sparqtest.domain.usecase.GetItemsUseCase
import com.sensate.sparqtest.domain.usecase.GetItemsWithRefreshUseCase
import com.sensate.sparqtest.domain.usecase.RefreshItemsUseCase
import com.sensate.sparqtest.presentation.common.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class ItemListViewModelTest {
    
    private lateinit var viewModel: ItemListViewModel
    private lateinit var getItemsUseCase: GetItemsUseCase
    private lateinit var getItemsWithRefreshUseCase: GetItemsWithRefreshUseCase
    private lateinit var refreshItemsUseCase: RefreshItemsUseCase
    private val testDispatcher = StandardTestDispatcher()
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getItemsUseCase = mock()
        getItemsWithRefreshUseCase = mock()
        refreshItemsUseCase = mock()
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `initial state is Loading`() = runTest {
        // Given
        val items = listOf(
            Item("1", "Test Title 1", "Test Description 1"),
            Item("2", "Test Title 2", "Test Description 2")
        )
        val successResult = Result.success(items)
        whenever(getItemsUseCase()).thenReturn(flowOf(items))
        whenever(getItemsWithRefreshUseCase()).thenReturn(flowOf(successResult))
        whenever(refreshItemsUseCase()).thenReturn(Result.success(true))
        
        // When
        viewModel = ItemListViewModel(getItemsUseCase, getItemsWithRefreshUseCase, refreshItemsUseCase)
        
        // Then
        assertEquals(UiState.Loading, viewModel.itemsState.value)
        
        // Advance the test dispatcher to allow the flow collection to happen
        testDispatcher.scheduler.advanceUntilIdle()
        
        val currentState = viewModel.itemsState.value
        assertTrue(currentState is UiState.Success)
        
        val loadedItems = (currentState as UiState.Success<List<Item>>).data
        assertEquals(2, loadedItems.size)
        assertEquals("Test Title 1", loadedItems[0].title)
    }
    
    @Test
    fun `refreshItems changes isRefreshing state and calls refreshItemsUseCase`() = runTest {
        // Given
        val items = listOf(
            Item("1", "Test Title 1", "Test Description 1")
        )
        val successResult = Result.success(items)
        whenever(getItemsUseCase()).thenReturn(flowOf(items))
        whenever(getItemsWithRefreshUseCase()).thenReturn(flowOf(successResult))
        whenever(refreshItemsUseCase()).thenReturn(Result.success(true))
        
        viewModel = ItemListViewModel(getItemsUseCase, getItemsWithRefreshUseCase, refreshItemsUseCase)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When
        viewModel.refreshItems()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        verify(refreshItemsUseCase).invoke()
        viewModel.isRefreshing.test {
            assertEquals(false, awaitItem())
        }
    }
    
    @Test
    fun `refreshItems updates state to Error when refresh fails`() = runTest {
        // Given
        val error = RuntimeException("Failed to refresh")
        val items = listOf(
            Item("1", "Test Title 1", "Test Description 1")
        )
        val successResult = Result.success(items)
        whenever(getItemsUseCase()).thenReturn(flowOf(items))
        whenever(getItemsWithRefreshUseCase()).thenReturn(flowOf(successResult))
        whenever(refreshItemsUseCase()).thenReturn(Result.failure(error))
        
        viewModel = ItemListViewModel(getItemsUseCase, getItemsWithRefreshUseCase, refreshItemsUseCase)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When
        viewModel.refreshItems()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        verify(refreshItemsUseCase).invoke()
        assertEquals("Failed to refresh", viewModel.networkMessage.value)
    }
} 