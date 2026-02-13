package com.mytask.data.repositories.project

import com.mytask.domain.model.Project
import kotlinx.coroutines.flow.StateFlow

interface ProjectRepository {
    // ═══════════════════════════════════════════════════════════════
    // SHARED STATE - Observable by ALL ViewModels
    // ═══════════════════════════════════════════════════════════════
    
    /** All projects list - shared across all screens */
    val projects: StateFlow<List<Project>>
    
    /** Currently selected/focused project */
    val selectedProject: StateFlow<Project?>
    
    /** Loading state */
    val isLoading: StateFlow<Boolean>
    
    /** Error state */
    val error: StateFlow<String?>
    
    // ═══════════════════════════════════════════════════════════════
    // ACTIONS - Called by ViewModels to modify state
    // ═══════════════════════════════════════════════════════════════
    
    /** Load projects (uses cache if available) */
    suspend fun loadProjects(): List<Project>
    
    /** Force refresh from remote */
    suspend fun refreshProjects(): List<Project>
    
    /** Select project by ID (for detail screens) */
    suspend fun selectProject(projectId: String)
    
    /** Clear current selection */
    fun clearSelection()
    
    /** Get single project by ID */
    suspend fun getProjectById(id: String): Project?
    
    /** Create new project */
    suspend fun createProject(project: Project): Project?
    
    /** Update existing project */
    suspend fun updateProject(project: Project): Project?
    
    /** Delete project */
    suspend fun deleteProject(id: String): Boolean
    
    /** Update project progress */
    suspend fun updateProjectProgress(id: String, progress: Int): Boolean
    
    /** Toggle project completion */
    suspend fun toggleProjectCompletion(id: String): Boolean
    
    /** Clear error state */
    fun clearError()
}