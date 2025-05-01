package com.sensate.sparqtest.data.repository

import com.sensate.sparqtest.data.local.dao.ItemDao
import com.sensate.sparqtest.data.local.entity.ItemEntity
import com.sensate.sparqtest.data.remote.api.ItemApiService
import com.sensate.sparqtest.domain.model.Item
import com.sensate.sparqtest.domain.repository.ItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import java.net.UnknownHostException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of the ItemRepository.
 * Uses both local (Room) and remote (Retrofit) data sources with offline-first approach.
 */
@Singleton
class ItemRepositoryImpl @Inject constructor(
    private val itemDao: ItemDao,
    private val apiService: ItemApiService
) : ItemRepository {
    
    override fun getItems(): Flow<List<Item>> {
        return itemDao.getAll().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    override fun getItemById(id: String): Flow<Item?> {
        return itemDao.getById(id).map { entity ->
            entity?.toDomainModel()
        }
    }
    
    override suspend fun refreshItems(): Result<Boolean> {
        return try {
            val remoteItems = apiService.getItems()

            val entities = remoteItems.map { 
                val domainModel = it.toDomainModel()
                ItemEntity.fromDomainModel(domainModel)
            }
            itemDao.replaceAll(entities)
            
            Result.success(true)
        } catch (e: UnknownHostException) {
            Result.failure(IOException("No internet connection. Please check your connection and try again.", e))
        } catch (e: IOException) {
            Result.failure(IOException("Network error. Please check your connection and try again.", e))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun hasLocalData(): Boolean {
        return itemDao.getAll().firstOrNull()?.isNotEmpty() ?: false
    }
    
    override fun getItemsWithRefresh(): Flow<Result<List<Item>>> = flow {
        val hasLocalData = hasLocalData()
        
        if (hasLocalData) {
            emitAll(getItems().map { Result.success(it) })
        } else {
            val refreshResult = refreshItems()
            
            if (refreshResult.isSuccess) {
                emitAll(getItems().map { Result.success(it) })
            } else {
                emit(Result.failure(refreshResult.exceptionOrNull() ?: Exception("Unknown error")))
            }
        }
    }
} 