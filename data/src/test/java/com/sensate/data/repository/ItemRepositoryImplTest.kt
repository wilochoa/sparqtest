package com.sensate.data.repository

import app.cash.turbine.test
import com.sensate.data.local.dao.ItemDao
import com.sensate.data.local.entity.ItemEntity
import com.sensate.data.remote.api.ItemApiService
import com.sensate.data.remote.model.ItemDto
import com.sensate.data.repository.ItemRepositoryImpl
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
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class ItemRepositoryImplTest {
    
    private lateinit var repository: ItemRepositoryImpl
    private lateinit var itemDao: ItemDao
    private lateinit var apiService: ItemApiService
    private val testDispatcher = StandardTestDispatcher()
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        itemDao = mock()
        apiService = mock()
        repository = ItemRepositoryImpl(itemDao, apiService)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `getItems returns mapped domain items from database`() = runTest {
        // Given
        val itemEntities = listOf(
            ItemEntity("1", "Test Title 1", "Test Description 1"),
            ItemEntity("2", "Test Title 2", "Test Description 2")
        )
        whenever(itemDao.getAll()).thenReturn(flowOf(itemEntities))
        
        // When & Then
        repository.getItems().test {
            // First emission should be the items from the database
            val items = awaitItem()
            assertEquals(2, items.size)
            assertEquals("Test Title 1", items[0].title)
            assertEquals("Test Description 1", items[0].description)
            assertEquals("1", items[0].id)
            awaitComplete()
        }
    }
    
    @Test
    fun `getItemById returns correct mapped domain item from database`() = runTest {
        // Given
        val itemEntity = ItemEntity("1", "Test Title", "Test Description")
        whenever(itemDao.getById("1")).thenReturn(flowOf(itemEntity))
        
        // When & Then
        repository.getItemById("1").test {
            val item = awaitItem()
            assertEquals("1", item?.id)
            assertEquals("Test Title", item?.title)
            assertEquals("Test Description", item?.description)
            awaitComplete()
        }
    }
    
    @Test
    fun `refreshItems fetches from API and stores in database`() = runTest {
        // Given
        val apiItems = listOf(
            ItemDto("Test Title 1", "Test Description 1"),
            ItemDto("Test Title 2", "Test Description 2")
        )
        whenever(apiService.getItems()).thenReturn(apiItems)
        
        // When
        val result = repository.refreshItems()
        
        // Then
        assertTrue(result.isSuccess)
        verify(itemDao).replaceAll(any())
    }
    
    @Test
    fun `refreshItems returns failure when API throws exception`() = runTest {
        // Given
        whenever(apiService.getItems()).thenThrow(RuntimeException("Network error"))
        
        // When
        val result = repository.refreshItems()
        
        // Then
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertEquals("Network error", exception?.message)
    }
} 