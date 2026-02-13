package com.mytask.data.repositories.assignment

import com.mytask.core.network.ApiResult
import com.mytask.data.local.dao.AssignmentDao
import com.mytask.data.remote.service.SheetsApiService
import com.mytask.domain.model.Assignment
import com.mytask.data.repositories.assignment.AssignmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class AssignmentRepositoryImpl(
    private val sheetsApi: SheetsApiService,
    private val assignmentDao: AssignmentDao
) : AssignmentRepository {
    
    override val assignments: Flow<List<Assignment>>
        get() = assignmentDao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }
    
    override val isLoading: Flow<Boolean>
        get() = kotlinx.coroutines.flow.flowOf(false) // Simplified for now
    
    override val error: Flow<String?>
        get() = kotlinx.coroutines.flow.flowOf(null) // Simplified for now
    
    override suspend fun loadAssignments(): ApiResult<Unit> {
        return try {
            // In a real implementation, this would fetch from Google Sheets
            // For now, we'll just return success
            ApiResult.Success(Unit)
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }
    
    override suspend fun getAssignmentById(id: String): Assignment? {
        return assignmentDao.getById(id)?.toDomain()
    }
    
    override suspend fun updateAssignmentCompletion(id: String, completed: Boolean): ApiResult<Unit> {
        return try {
            // Update local database
            assignmentDao.updateCompleted(id, completed)
            
            // In a real implementation, this would also update Google Sheets
            // For now, we'll just return success
            ApiResult.Success(Unit)
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }
    
    override suspend fun createAssignment(assignment: Assignment): ApiResult<Assignment> {
        return try {
            val entity = assignment.copy(id = java.util.UUID.randomUUID().toString()).toEntity()
            assignmentDao.upsert(entity)
            ApiResult.Success(entity.toDomain())
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }
    
    override suspend fun updateAssignment(assignment: Assignment): ApiResult<Assignment> {
        return try {
            assignmentDao.upsert(assignment.toEntity())
            ApiResult.Success(assignment)
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }
    
    override suspend fun deleteAssignment(id: String): ApiResult<Unit> {
        return try {
            assignmentDao.deleteById(id)
            ApiResult.Success(Unit)
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }
}

// Extension functions for entity/domain conversion
@OptIn(ExperimentalTime::class)
fun AssignmentEntity.toDomain(): Assignment {
    return Assignment(
        id = id,
        title = title,
        description = description,
        dueDate = dueDateMillis?.let { Instant.fromEpochMilliseconds(it) },
        subject = subject,
        completed = completed,
        priority = priority
    )
}

@OptIn(ExperimentalTime::class)
fun Assignment.toEntity(): AssignmentEntity {
    return AssignmentEntity(
        id = id,
        title = title,
        description = description,
        dueDateMillis = dueDate?.toEpochMilliseconds(),
        subject = subject,
        completed = completed,
        priority = priority
    )
}