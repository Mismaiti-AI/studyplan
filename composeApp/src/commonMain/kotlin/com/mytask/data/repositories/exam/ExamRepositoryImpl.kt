package com.mytask.data.repositories.exam

import com.mytask.data.local.dao.ExamDao
import com.mytask.data.local.entity.ExamEntity
import com.mytask.data.remote.service.SheetsApiService
import com.mytask.domain.model.Exam
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
class ExamRepositoryImpl(
    private val sheetsApi: SheetsApiService,
    private val examDao: ExamDao
) : ExamRepository {

    private val repositoryScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    // ═══════════════════════════════════════════════════════════════
    // STATE MANAGEMENT - Repository holds state
    // ═══════════════════════════════════════════════════════════════

    override val exams: StateFlow<List<Exam>> = examDao.getAll()
        .map { entities -> entities.map { it.toDomain() } }
        .stateIn(repositoryScope, SharingStarted.Eagerly, emptyList())

    private val _selectedExam = MutableStateFlow<Exam?>(null)
    override val selectedExam: StateFlow<Exam?> = _selectedExam.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    override val error: StateFlow<String?> = _error.asStateFlow()

    // ═══════════════════════════════════════════════════════════════
    // ACTIONS
    // ═══════════════════════════════════════════════════════════════

    override suspend fun loadExams(): List<Exam> {
        _isLoading.value = true
        _error.value = null
        return try {
            exams.value
        } catch (e: Exception) {
            _error.value = e.message ?: "Unknown error loading exams"
            emptyList()
        } finally {
            _isLoading.value = false
        }
    }

    override suspend fun refreshExams(): List<Exam> {
        return loadExams()
    }

    override suspend fun selectExam(examId: String) {
        val exam = examDao.getById(examId)?.toDomain()
        _selectedExam.value = exam
    }

    override fun clearSelection() {
        _selectedExam.value = null
    }

    override suspend fun getExamById(id: String): Exam? {
        return examDao.getById(id)?.toDomain()
    }

    override suspend fun createExam(exam: Exam): Exam? {
        _isLoading.value = true
        _error.value = null
        return try {
            val newExam = if (exam.id.isEmpty()) {
                exam.copy(id = generateId())
            } else {
                exam
            }
            val entity = newExam.toEntity()
            examDao.upsert(entity)
            newExam
        } catch (e: Exception) {
            _error.value = e.message ?: "Unknown error creating exam"
            null
        } finally {
            _isLoading.value = false
        }
    }

    override suspend fun updateExam(exam: Exam): Exam? {
        _isLoading.value = true
        _error.value = null
        return try {
            examDao.upsert(exam.toEntity())
            exam
        } catch (e: Exception) {
            _error.value = e.message ?: "Unknown error updating exam"
            null
        } finally {
            _isLoading.value = false
        }
    }

    override suspend fun deleteExam(id: String): Boolean {
        _isLoading.value = true
        _error.value = null
        return try {
            examDao.deleteById(id)
            true
        } catch (e: Exception) {
            _error.value = e.message ?: "Unknown error deleting exam"
            false
        } finally {
            _isLoading.value = false
        }
    }

    override suspend fun toggleExamPreparationStatus(id: String): Boolean {
        _isLoading.value = true
        _error.value = null
        return try {
            val exam = examDao.getById(id)?.toDomain()
            if (exam != null) {
                val updated = exam.copy(preparationStatus = !exam.preparationStatus)
                examDao.upsert(updated.toEntity())
                true
            } else {
                _error.value = "Exam not found"
                false
            }
        } catch (e: Exception) {
            _error.value = e.message ?: "Unknown error toggling preparation status"
            false
        } finally {
            _isLoading.value = false
        }
    }

    override fun clearError() {
        _error.value = null
    }

    private fun generateId(): String {
        return "exam_${kotlin.time.Clock.System.now().toEpochMilliseconds()}"
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
