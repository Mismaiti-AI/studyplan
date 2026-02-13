package com.mytask.data.repositories.project

import com.mytask.core.network.ApiResult
import com.mytask.data.local.dao.ProjectDao
import com.mytask.data.remote.service.SheetsApiService
import com.mytask.domain.model.Project
import com.mytask.data.repositories.project.ProjectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class ProjectRepositoryImpl(
    private val sheetsApi: SheetsApiService,
    private val projectDao: ProjectDao
) : ProjectRepository {
    
    override val projects: Flow<List<Project>>
        get() = projectDao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }
    
    override val isLoading: Flow<Boolean>
        get() = kotlinx.coroutines.flow.flowOf(false) // Simplified for now
    
    override val error: Flow<String?>
        get() = kotlinx.coroutines.flow.flowOf(null) // Simplified for now
    
    override suspend fun loadProjects(): ApiResult<Unit> {
        return try {
            // In a real implementation, this would fetch from Google Sheets
            // For now, we'll just return success
            ApiResult.Success(Unit)
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }
    
    override suspend fun getProjectById(id: String): Project? {
        return projectDao.getById(id)?.toDomain()
    }
    
    override suspend fun updateProjectProgress(id: String, progress: Int, completed: Boolean): ApiResult<Unit> {
        return try {
            // Update local database
            projectDao.updateProgress(id, progress, completed)
            
            // In a real implementation, this would also update Google Sheets
            // For now, we'll just return success
            ApiResult.Success(Unit)
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }
    
    override suspend fun createProject(project: Project): ApiResult<Project> {
        return try {
            val entity = project.copy(id = java.util.UUID.randomUUID().toString()).toEntity()
            projectDao.upsert(entity)
            ApiResult.Success(entity.toDomain())
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }
    
    override suspend fun updateProject(project: Project): ApiResult<Project> {
        return try {
            projectDao.upsert(project.toEntity())
            ApiResult.Success(project)
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }
    
    override suspend fun deleteProject(id: String): ApiResult<Unit> {
        return try {
            projectDao.deleteById(id)
            ApiResult.Success(Unit)
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }
}

// Extension functions for entity/domain conversion
@OptIn(ExperimentalTime::class)
fun ProjectEntity.toDomain(): Project {
    return Project(
        id = id,
        title = title,
        description = description,
        startDate = startDateMillis?.let { Instant.fromEpochMilliseconds(it) },
        dueDate = dueDateMillis?.let { Instant.fromEpochMilliseconds(it) },
        subject = subject,
        progress = progress,
        completed = completed
    )
}

@OptIn(ExperimentalTime::class)
fun Project.toEntity(): ProjectEntity {
    return ProjectEntity(
        id = id,
        title = title,
        description = description,
        startDateMillis = startDate?.toEpochMilliseconds(),
        dueDateMillis = dueDate?.toEpochMilliseconds(),
        subject = subject,
        progress = progress,
        completed = completed
    )
}