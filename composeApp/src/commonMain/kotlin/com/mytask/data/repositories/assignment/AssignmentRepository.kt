package com.mytask.data.repositories.assignment

import com.mytask.domain.model.Assignment
import kotlinx.coroutines.flow.StateFlow

interface AssignmentRepository {
    // ═══════════════════════════════════════════════════════════════
    // SHARED STATE - Observable by ALL ViewModels
    // ═══════════════════════════════════════════════════════════════
    
    /** All assignments list - shared across all screens */
    val assignments: StateFlow<List<Assignment>>
    
    /** Currently selected/focused assignment */
    val selectedAssignment: StateFlow<Assignment?>
    
    /** Loading state */
    val isLoading: StateFlow<Boolean>
    
    /** Error state */
    val error: StateFlow<String?>
    
    // ═══════════════════════════════════════════════════════════════
    // ACTIONS - Called by ViewModels to modify state
    // ═══════════════════════════════════════════════════════════════
    
    /** Load assignments (uses cache if available) */
    suspend fun loadAssignments(): List<Assignment>
    
    /** Force refresh from remote */
    suspend fun refreshAssignments(): List<Assignment>
    
    /** Select assignment by ID (for detail screens) */
    suspend fun selectAssignment(assignmentId: String)
    
    /** Clear current selection */
    fun clearSelection()
    
    /** Get single assignment by ID */
    suspend fun getAssignmentById(id: String): Assignment?
    
    /** Create new assignment */
    suspend fun createAssignment(assignment: Assignment): Assignment?
    
    /** Update existing assignment */
    suspend fun updateAssignment(assignment: Assignment): Assignment?
    
    /** Delete assignment */
    suspend fun deleteAssignment(id: String): Boolean
    
    /** Toggle assignment completion */
    suspend fun toggleAssignmentCompletion(id: String): Boolean
    
    /** Clear error state */
    fun clearError()
}