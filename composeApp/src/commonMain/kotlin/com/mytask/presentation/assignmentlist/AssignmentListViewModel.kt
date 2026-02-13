package com.mytask.presentation.assignmentlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mytask.data.repositories.assignment.AssignmentRepository
import com.mytask.domain.model.Assignment
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Define UiState in the same file as the ViewModel
sealed interface AssignmentListUiState {
    data object Loading : AssignmentListUiState
    data class Success(val assignments: List<Assignment>) : AssignmentListUiState
    data class Error(val message: String) : AssignmentListUiState
}

class AssignmentListViewModel(
    private val assignmentRepository: AssignmentRepository
) : ViewModel() {

    val uiState: StateFlow<AssignmentListUiState> = combine(
        assignmentRepository.assignments,
        assignmentRepository.isLoading,
        assignmentRepository.error
    ) { assignments, isLoading, error ->
        when {
            isLoading -> AssignmentListUiState.Loading
            error != null -> AssignmentListUiState.Error(error)
            else -> AssignmentListUiState.Success(assignments)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AssignmentListUiState.Loading
    )

    fun loadAssignments() {
        viewModelScope.launch {
            assignmentRepository.loadAssignments()
        }
    }

    fun toggleAssignmentCompletion(id: String, completed: Boolean) {
        viewModelScope.launch {
            assignmentRepository.updateAssignmentCompletion(id, completed)
        }
    }
}