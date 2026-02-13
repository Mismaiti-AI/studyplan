package com.mytask.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mytask.data.repositories.assignment.AssignmentRepository
import com.mytask.data.repositories.exam.ExamRepository
import com.mytask.data.repositories.project.ProjectRepository
import com.mytask.domain.model.Assignment
import com.mytask.domain.model.Exam
import com.mytask.domain.model.Project
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
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
    data class Success(
        val upcomingAssignments: List<Assignment>,
        val upcomingExams: List<Exam>,
        val upcomingProjects: List<Project>
    ) : DashboardUiState
    data class Error(val message: String) : DashboardUiState
}

@OptIn(ExperimentalTime::class)
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
                val now = Clock.System.now()
                val upcomingAssignments = assignments.filter {
                    !it.completed && it.dueDate != null && it.dueDate >= now
                }.take(5)
                val upcomingExams = exams.filter { it.isUpcoming }.take(5)
                val upcomingProjects = projects.filter {
                    !it.completed && it.dueDate != null && it.dueDate >= now
                }.take(5)

                DashboardUiState.Success(
                    upcomingAssignments = upcomingAssignments,
                    upcomingExams = upcomingExams,
                    upcomingProjects = upcomingProjects
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState.Loading
    )

    fun loadDashboardData() {
        viewModelScope.launch {
            assignmentRepository.loadAssignments()
            examRepository.loadExams()
            projectRepository.loadProjects()
        }
    }
}