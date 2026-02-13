package com.mytask.presentation.projectlist

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
sealed interface ProjectListUiState {
    data object Loading : ProjectListUiState
    data class Success(val projects: List<Project>) : ProjectListUiState
    data class Error(val message: String) : ProjectListUiState
}

class ProjectListViewModel(
    private val projectRepository: ProjectRepository
) : ViewModel() {

    val uiState: StateFlow<ProjectListUiState> = combine(
        projectRepository.projects,
        projectRepository.isLoading,
        projectRepository.error
    ) { projects, isLoading, error ->
        when {
            isLoading -> ProjectListUiState.Loading
            error != null -> ProjectListUiState.Error(error)
            else -> ProjectListUiState.Success(projects)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProjectListUiState.Loading
    )

    fun loadProjects() {
        viewModelScope.launch {
            projectRepository.loadProjects()
        }
    }

    fun updateProjectProgress(id: String, progress: Int, completed: Boolean) {
        viewModelScope.launch {
            projectRepository.updateProjectProgress(id, progress, completed)
        }
    }
}