package com.mytask.presentation.assignmentdetail

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
sealed interface AssignmentDetailUiState {
    data object Loading : AssignmentDetailUiState
    data class Success(val assignment: Assignment) : AssignmentDetailUiState
    data class Error(val message: String) : AssignmentDetailUiState
}

class AssignmentDetailViewModel(
    private val assignmentId: String,
    private val assignmentRepository: AssignmentRepository
) : ViewModel() {

    init {
        loadAssignment()
    }

    val uiState: StateFlow<AssignmentDetailUiState> = combine(
        assignmentRepository.selectedAssignment,
        assignmentRepository.isLoading,
        assignmentRepository.error
    ) { assignment, isLoading, error ->
        when {
            isLoading -> AssignmentDetailUiState.Loading
            error != null -> AssignmentDetailUiState.Error(error)
            assignment != null -> AssignmentDetailUiState.Success(assignment)
            else -> AssignmentDetailUiState.Loading
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AssignmentDetailUiState.Loading
    )

    fun loadAssignment() {
        viewModelScope.launch {
            assignmentRepository.selectAssignment(assignmentId)
        }
    }

    fun toggleCompleted() {
        viewModelScope.launch {
            assignmentRepository.toggleAssignmentCompletion(assignmentId)
        }
    }
}