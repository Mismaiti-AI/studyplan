package com.mytask.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.mytask.data.local.entity.ProjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY due_date_millis ASC")
    fun getAll(): Flow<List<ProjectEntity>>
    
    @Query("SELECT * FROM projects WHERE id = :id")
    suspend fun getById(id: String): ProjectEntity?
    
    @Query("SELECT * FROM projects WHERE completed = 0 ORDER BY due_date_millis ASC")
    fun getActive(): Flow<List<ProjectEntity>>
    
    @Query("SELECT * FROM projects WHERE completed = 1 ORDER BY due_date_millis ASC")
    fun getCompleted(): Flow<List<ProjectEntity>>
    
    @Upsert
    suspend fun upsert(project: ProjectEntity)
    
    @Upsert
    suspend fun upsertAll(projects: List<ProjectEntity>)
    
    @Query("UPDATE projects SET progress = :progress, completed = :completed WHERE id = :id")
    suspend fun updateProgress(id: String, progress: Int, completed: Boolean)
    
    @Query("DELETE FROM projects WHERE id = :id")
    suspend fun deleteById(id: String)
    
    @Query("DELETE FROM projects")
    suspend fun deleteAll()
}