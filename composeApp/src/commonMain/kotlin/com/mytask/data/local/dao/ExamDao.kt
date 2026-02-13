package com.mytask.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.mytask.data.local.entity.ExamEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExamDao {
    @Query("SELECT * FROM exams ORDER BY exam_date_millis ASC")
    fun getAll(): Flow<List<ExamEntity>>
    
    @Query("SELECT * FROM exams WHERE id = :id")
    suspend fun getById(id: String): ExamEntity?
    
    @Query("SELECT * FROM exams WHERE exam_date_millis >= :currentTimestamp ORDER BY exam_date_millis ASC")
    fun getUpcoming(currentTimestamp: Long): Flow<List<ExamEntity>>
    
    @Query("SELECT * FROM exams WHERE exam_date_millis < :currentTimestamp ORDER BY exam_date_millis DESC")
    fun getPast(currentTimestamp: Long): Flow<List<ExamEntity>>
    
    @Upsert
    suspend fun upsert(exam: ExamEntity)
    
    @Upsert
    suspend fun upsertAll(exams: List<ExamEntity>)
    
    @Query("UPDATE exams SET preparation_status = :status WHERE id = :id")
    suspend fun updatePreparationStatus(id: String, status: Boolean)
    
    @Query("DELETE FROM exams WHERE id = :id")
    suspend fun deleteById(id: String)
    
    @Query("DELETE FROM exams")
    suspend fun deleteAll()
}