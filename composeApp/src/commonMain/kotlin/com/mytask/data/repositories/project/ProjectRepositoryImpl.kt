package com.mytask.data.repositories.project

import com.mytask.data.local.dao.ProjectDao
import com.mytask.data.local.entity.ProjectEntity
import com.mytask.data.remote.service.SheetsApiService
import com.mytask.domain.model.Project
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class ProjectRepositoryImpl(
    private val sheetsApi: SheetsApiService,
    private val projectDao: ProjectDao
) : ProjectRepository {

    private val repositoryScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    // ═══════════════════════════════════════════════════════════════
    // STATE MANAGEMENT - Repository holds state
    // ═══════════════════════════════════════════════════════════════

    override val projects: StateFlow<List<Project>> = projectDao.getAll()
        .map { entities -> entities.map { it.toDomain() } }
        .stateIn(repositoryScope, SharingStarted.Eagerly, emptyList())

    private val _selectedProject = MutableStateFlow<Project?>(null)
    override val selectedProject: StateFlow<Project?> = _selectedProject.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    override val error: StateFlow<String?> = _error.asStateFlow()

    // ═══════════════════════════════════════════════════════════════
    // ACTIONS
    // ═══════════════════════════════════════════════════════════════

    override suspend fun loadProjects(): List<Project> {
        _isLoading.value = true
        _error.value = null
        return try {
            val entities = projectDao.getAllOnce()
            entities.map { it.toDomain() }
        } catch (e: Exception) {
            _error.value = e.message ?: "Unknown error loading projects"
            emptyList()
        } finally {
            _isLoading.value = false
        }
    }

    override suspend fun refreshProjects(): List<Project> {
        return loadProjects()
    }

    override suspend fun selectProject(projectId: String) {
        val project = projectDao.getById(projectId)?.toDomain()
        _selectedProject.value = project
    }

    override fun clearSelection() {
        _selectedProject.value = null
    }

    override suspend fun getProjectById(id: String): Project? {
        return projectDao.getById(id)?.toDomain()
    }

    override suspend fun createProject(project: Project): Project? {
        _isLoading.value = true
        _error.value = null
        return try {
            val newProject = if (project.id.isEmpty()) {
                project.copy(id = generateId())
            } else {
                project
            }
            val entity = newProject.toEntity()
            projectDao.upsert(entity)
            newProject
        } catch (e: Exception) {
            _error.value = e.message ?: "Unknown error creating project"
            null
        } finally {
            _isLoading.value = false
        }
    }

    override suspend fun updateProject(project: Project): Project? {
        _isLoading.value = true
        _error.value = null
        return try {
            projectDao.upsert(project.toEntity())
            project
        } catch (e: Exception) {
            _error.value = e.message ?: "Unknown error updating project"
            null
        } finally {
            _isLoading.value = false
        }
    }

    override suspend fun deleteProject(id: String): Boolean {
        _isLoading.value = true
        _error.value = null
        return try {
            projectDao.deleteById(id)
            true
        } catch (e: Exception) {
            _error.value = e.message ?: "Unknown error deleting project"
            false
        } finally {
            _isLoading.value = false
        }
    }

    override suspend fun updateProjectProgress(id: String, progress: Int): Boolean {
        _isLoading.value = true
        _error.value = null
        return try {
            val project = projectDao.getById(id)?.toDomain()
            if (project != null) {
                val updated = project.copy(
                    progress = progress.coerceIn(0, 100),
                    completed = progress >= 100
                )
                projectDao.upsert(updated.toEntity())
                true
            } else {
                _error.value = "Project not found"
                false
            }
        } catch (e: Exception) {
            _error.value = e.message ?: "Unknown error updating progress"
            false
        } finally {
            _isLoading.value = false
        }
    }

    override suspend fun toggleProjectCompletion(id: String): Boolean {
        _isLoading.value = true
        _error.value = null
        return try {
            val project = projectDao.getById(id)?.toDomain()
            if (project != null) {
                val updated = project.copy(
                    completed = !project.completed,
                    progress = if (!project.completed) 100 else project.progress
                )
                projectDao.upsert(updated.toEntity())
                true
            } else {
                _error.value = "Project not found"
                false
            }
        } catch (e: Exception) {
            _error.value = e.message ?: "Unknown error toggling completion"
            false
        } finally {
            _isLoading.value = false
        }
    }

    override fun clearError() {
        _error.value = null
    }

    private fun generateId(): String {
        return "project_${kotlin.time.Clock.System.now().toEpochMilliseconds()}"
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
