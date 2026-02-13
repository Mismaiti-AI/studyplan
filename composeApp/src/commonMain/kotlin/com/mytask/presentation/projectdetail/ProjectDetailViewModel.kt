package com.mytask.presentation.projectdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mytask.data.repositories.project.ProjectRepository
import com.mytask.domain.model.Project
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Define UiState in the same file as the ViewModel
sealed interface ProjectDetailUiState {
    data object Loading : ProjectDetailUiState
    data class Success(val project: Project) : ProjectDetailUiState
    data class Error(val message: String) : ProjectDetailUiState
}

class ProjectDetailViewModel(
    private val projectRepository: ProjectRepository
) : ViewModel() {

    private var currentProjectId: String? = null
    private var _project: Project? = null

    val uiState: StateFlow<ProjectDetailUiState> = combine(
        projectRepository.isLoading,
        projectRepository.error
    ) { isLoading, error ->
        when {
            isLoading -> ProjectDetailUiState.Loading
            error != null -> ProjectDetailUiState.Error(error)
            _project != null -> ProjectDetailUiState.Success(_project!!)
            else -> ProjectDetailUiState.Loading
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProjectDetailUiState.Loading
    )

    fun loadProject(id: String) {
        currentProjectId = id
        viewModelScope.launch {
            val project = projectRepository.getProjectById(id)
            if (project != null) {
                _project = project
            }
        }
    }

    fun updateProgress(progress: Int, completed: Boolean) {
        currentProjectId?.let { id ->
            viewModelScope.launch {
                projectRepository.updateProjectProgress(id, progress, completed)
            }
        }
    }
}