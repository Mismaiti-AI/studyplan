package com.mytask.data.repositories.exam

import com.mytask.core.network.ApiResult
import com.mytask.data.local.dao.ExamDao
import com.mytask.data.remote.service.SheetsApiService
import com.mytask.domain.model.Exam
import com.mytask.data.repositories.exam.ExamRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class ExamRepositoryImpl(
    private val sheetsApi: SheetsApiService,
    private val examDao: ExamDao
) : ExamRepository {
    
    override val exams: Flow<List<Exam>>
        get() = examDao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }
    
    override val isLoading: Flow<Boolean>
        get() = kotlinx.coroutines.flow.flowOf(false) // Simplified for now
    
    override val error: Flow<String?>
        get() = kotlinx.coroutines.flow.flowOf(null) // Simplified for now
    
    override suspend fun loadExams(): ApiResult<Unit> {
        return try {
            // In a real implementation, this would fetch from Google Sheets
            // For now, we'll just return success
            ApiResult.Success(Unit)
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }
    
    override suspend fun getExamById(id: String): Exam? {
        return examDao.getById(id)?.toDomain()
    }
    
    override suspend fun updateExamPreparationStatus(id: String, status: Boolean): ApiResult<Unit> {
        return try {
            // Update local database
            examDao.updatePreparationStatus(id, status)
            
            // In a real implementation, this would also update Google Sheets
            // For now, we'll just return success
            ApiResult.Success(Unit)
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }
    
    override suspend fun createExam(exam: Exam): ApiResult<Exam> {
        return try {
            val entity = exam.copy(id = java.util.UUID.randomUUID().toString()).toEntity()
            examDao.upsert(entity)
            ApiResult.Success(entity.toDomain())
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }
    
    override suspend fun updateExam(exam: Exam): ApiResult<Exam> {
        return try {
            examDao.upsert(exam.toEntity())
            ApiResult.Success(exam)
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }
    
    override suspend fun deleteExam(id: String): ApiResult<Unit> {
        return try {
            examDao.deleteById(id)
            ApiResult.Success(Unit)
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }
}

// Extension functions for entity/domain conversion
@OptIn(ExperimentalTime::class)
fun ExamEntity.toDomain(): Exam {
    return Exam(
        id = id,
        title = title,
        subject = subject,
        examDate = examDateMillis?.let { Instant.fromEpochMilliseconds(it) },
        description = description,
        preparationStatus = preparationStatus
    )
}

@OptIn(ExperimentalTime::class)
fun Exam.toEntity(): ExamEntity {
    return ExamEntity(
        id = id,
        title = title,
        subject = subject,
        examDateMillis = examDate?.toEpochMilliseconds(),
        description = description,
        preparationStatus = preparationStatus
    )
}