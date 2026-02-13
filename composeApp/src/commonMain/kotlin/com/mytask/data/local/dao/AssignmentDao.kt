package com.mytask.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.mytask.data.local.entity.AssignmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AssignmentDao {
    @Query("SELECT * FROM assignments ORDER BY due_date_millis ASC")
    fun getAll(): Flow<List<AssignmentEntity>>
    
    @Query("SELECT * FROM assignments WHERE id = :id")
    suspend fun getById(id: String): AssignmentEntity?
    
    @Query("SELECT * FROM assignments WHERE completed = 0 ORDER BY due_date_millis ASC")
    fun getIncomplete(): Flow<List<AssignmentEntity>>
    
    @Query("SELECT * FROM assignments WHERE completed = 1 ORDER BY due_date_millis ASC")
    fun getCompleted(): Flow<List<AssignmentEntity>>
    
    @Upsert
    suspend fun upsert(assignment: AssignmentEntity)
    
    @Upsert
    suspend fun upsertAll(assignments: List<AssignmentEntity>)
    
    @Query("UPDATE assignments SET completed = :completed WHERE id = :id")
    suspend fun updateCompleted(id: String, completed: Boolean)
    
    @Query("DELETE FROM assignments WHERE id = :id")
    suspend fun deleteById(id: String)
    
    @Query("DELETE FROM assignments")
    suspend fun deleteAll()
}