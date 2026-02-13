package com.mytask.data.repositories.exam

import com.mytask.domain.model.Exam
import kotlinx.coroutines.flow.StateFlow

interface ExamRepository {
    // ═══════════════════════════════════════════════════════════════
    // SHARED STATE - Observable by ALL ViewModels
    // ═══════════════════════════════════════════════════════════════
    
    /** All exams list - shared across all screens */
    val exams: StateFlow<List<Exam>>
    
    /** Currently selected/focused exam */
    val selectedExam: StateFlow<Exam?>
    
    /** Loading state */
    val isLoading: StateFlow<Boolean>
    
    /** Error state */
    val error: StateFlow<String?>
    
    // ═══════════════════════════════════════════════════════════════
    // ACTIONS - Called by ViewModels to modify state
    // ═══════════════════════════════════════════════════════════════
    
    /** Load exams (uses cache if available) */
    suspend fun loadExams(): List<Exam>
    
    /** Force refresh from remote */
    suspend fun refreshExams(): List<Exam>
    
    /** Select exam by ID (for detail screens) */
    suspend fun selectExam(examId: String)
    
    /** Clear current selection */
    fun clearSelection()
    
    /** Get single exam by ID */
    suspend fun getExamById(id: String): Exam?
    
    /** Create new exam */
    suspend fun createExam(exam: Exam): Exam?
    
    /** Update existing exam */
    suspend fun updateExam(exam: Exam): Exam?
    
    /** Delete exam */
    suspend fun deleteExam(id: String): Boolean
    
    /** Toggle exam preparation status */
    suspend fun toggleExamPreparationStatus(id: String): Boolean
    
    /** Clear error state */
    fun clearError()
}