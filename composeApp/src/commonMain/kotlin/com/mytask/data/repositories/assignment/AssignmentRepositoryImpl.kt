package com.mytask.data.repositories.assignment

import com.mytask.data.local.dao.AssignmentDao
import com.mytask.data.local.entity.AssignmentEntity
import com.mytask.data.remote.service.SheetsApiService
import com.mytask.domain.model.Assignment
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
class AssignmentRepositoryImpl(
    private val sheetsApi: SheetsApiService,
    private val assignmentDao: AssignmentDao
) : AssignmentRepository {

    private val repositoryScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    // ═══════════════════════════════════════════════════════════════
    // STATE MANAGEMENT - Repository holds state
    // ═══════════════════════════════════════════════════════════════

    override val assignments: StateFlow<List<Assignment>> = assignmentDao.getAll()
        .map { entities -> entities.map { it.toDomain() } }
        .stateIn(repositoryScope, SharingStarted.Eagerly, emptyList())

    private val _selectedAssignment = MutableStateFlow<Assignment?>(null)
    override val selectedAssignment: StateFlow<Assignment?> = _selectedAssignment.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    override val error: StateFlow<String?> = _error.asStateFlow()

    // ═══════════════════════════════════════════════════════════════
    // ACTIONS
    // ═══════════════════════════════════════════════════════════════

    override suspend fun loadAssignments(): List<Assignment> {
        _isLoading.value = true
        _error.value = null
        return try {
            // In a real implementation, this would fetch from Google Sheets
            // For now, we'll just return the locally stored assignments
            assignments.value
        } catch (e: Exception) {
            _error.value = e.message ?: "Unknown error loading assignments"
            emptyList()
        } finally {
            _isLoading.value = false
        }
    }

    override suspend fun refreshAssignments(): List<Assignment> {
        // Force refresh from remote - same as load for now
        return loadAssignments()
    }

    override suspend fun selectAssignment(assignmentId: String) {
        val assignment = assignmentDao.getById(assignmentId)?.toDomain()
        _selectedAssignment.value = assignment
    }

    override fun clearSelection() {
        _selectedAssignment.value = null
    }

    override suspend fun getAssignmentById(id: String): Assignment? {
        return assignmentDao.getById(id)?.toDomain()
    }

    override suspend fun createAssignment(assignment: Assignment): Assignment? {
        _isLoading.value = true
        _error.value = null
        return try {
            val newAssignment = if (assignment.id.isEmpty()) {
                assignment.copy(id = generateId())
            } else {
                assignment
            }
            val entity = newAssignment.toEntity()
            assignmentDao.upsert(entity)
            newAssignment
        } catch (e: Exception) {
            _error.value = e.message ?: "Unknown error creating assignment"
            null
        } finally {
            _isLoading.value = false
        }
    }

    override suspend fun updateAssignment(assignment: Assignment): Assignment? {
        _isLoading.value = true
        _error.value = null
        return try {
            assignmentDao.upsert(assignment.toEntity())
            assignment
        } catch (e: Exception) {
            _error.value = e.message ?: "Unknown error updating assignment"
            null
        } finally {
            _isLoading.value = false
        }
    }

    override suspend fun deleteAssignment(id: String): Boolean {
        _isLoading.value = true
        _error.value = null
        return try {
            assignmentDao.deleteById(id)
            true
        } catch (e: Exception) {
            _error.value = e.message ?: "Unknown error deleting assignment"
            false
        } finally {
            _isLoading.value = false
        }
    }

    override suspend fun toggleAssignmentCompletion(id: String): Boolean {
        _isLoading.value = true
        _error.value = null
        return try {
            val assignment = assignmentDao.getById(id)?.toDomain()
            if (assignment != null) {
                val updated = assignment.copy(completed = !assignment.completed)
                assignmentDao.upsert(updated.toEntity())
                true
            } else {
                _error.value = "Assignment not found"
                false
            }
        } catch (e: Exception) {
            _error.value = e.message ?: "Unknown error toggling assignment completion"
            false
        } finally {
            _isLoading.value = false
        }
    }

    override fun clearError() {
        _error.value = null
    }

    private fun generateId(): String {
        return "assignment_${kotlin.time.Clock.System.now().toEpochMilliseconds()}"
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
