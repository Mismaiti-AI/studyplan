package com.mytask.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.mytask.data.local.entity.AppConfigEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppConfigDao {
    @Query("SELECT * FROM app_configs WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): AppConfigEntity?
    
    @Query("SELECT * FROM app_configs LIMIT 1")
    fun getConfig(): Flow<AppConfigEntity?>
    
    @Upsert
    suspend fun upsert(config: AppConfigEntity)
    
    @Query("DELETE FROM app_configs WHERE id = :id")
    suspend fun deleteById(id: String)
    
    @Query("DELETE FROM app_configs")
    suspend fun deleteAll()
}