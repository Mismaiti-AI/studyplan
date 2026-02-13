package com.mytask.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mytask.data.repositories.assignment.AssignmentRepository
import com.mytask.data.repositories.exam.ExamRepository
import com.mytask.data.repositories.project.ProjectRepository
import com.mytask.domain.model.Assignment
import com.mytask.domain.model.Exam
import com.mytask.domain.model.Project
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Define UiState in the same file as the ViewModel
data class DashboardData(
    val assignments: List<Assignment> = emptyList(),
    val exams: List<Exam> = emptyList(),
    val projects: List<Project> = emptyList(),
    val upcomingItems: List<Any> = emptyList()
)

sealed interface DashboardUiState {
    data object Loading : DashboardUiState
    data class Success(val data: DashboardData) : DashboardUiState
    data class Error(val message: String) : DashboardUiState
}

class DashboardViewModel(
    private val assignmentRepository: AssignmentRepository,
    private val examRepository: ExamRepository,
    private val projectRepository: ProjectRepository
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = combine(
        assignmentRepository.assignments,
        examRepository.exams,
        projectRepository.projects,
        assignmentRepository.isLoading,
        examRepository.isLoading,
        projectRepository.isLoading,
        assignmentRepository.error,
        examRepository.error,
        projectRepository.error
    ) { assignments, exams, projects, _, _, _, error1, error2, error3 ->
        val combinedError = listOfNotNull(error1, error2, error3).firstOrNull()
        
        when {
            combinedError != null -> DashboardUiState.Error(combinedError)
            else -> {
                val upcomingItems = mutableListOf<Any>()
                upcomingItems.addAll(assignments.filter { !it.completed && it.dueDate != null && 
                    it.dueDate >= kotlin.time.Instant.fromEpochMilliseconds(System.currentTimeMillis()) })
                upcomingItems.addAll(exams.filter { it.isUpcoming })
                upcomingItems.addAll(projects.filter { !it.completed && it.dueDate != null && 
                    it.dueDate >= kotlin.time.Instant.fromEpochMilliseconds(System.currentTimeMillis()) })
                
                val dashboardData = DashboardData(
                    assignments = assignments,
                    exams = exams,
                    projects = projects,
                    upcomingItems = upcomingItems.sortedBy { 
                        when (it) {
                            is Assignment -> it.dueDate?.toEpochMilliseconds() ?: Long.MAX_VALUE
                            is Exam -> it.examDate?.toEpochMilliseconds() ?: Long.MAX_VALUE
                            is Project -> it.dueDate?.toEpochMilliseconds() ?: Long.MAX_VALUE
                            else -> Long.MAX_VALUE
                        }
                    }
                )
                DashboardUiState.Success(dashboardData)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState.Loading
    )

    fun loadAllData() {
        viewModelScope.launch {
            assignmentRepository.loadAssignments()
            examRepository.loadExams()
            projectRepository.loadProjects()
        }
    }
}