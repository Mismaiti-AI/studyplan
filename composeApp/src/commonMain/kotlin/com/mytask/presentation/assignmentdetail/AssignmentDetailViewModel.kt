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
    private val assignmentRepository: AssignmentRepository
) : ViewModel() {

    private var currentAssignmentId: String? = null
    private var _assignment: Assignment? = null

    val uiState: StateFlow<AssignmentDetailUiState> = combine(
        assignmentRepository.isLoading,
        assignmentRepository.error
    ) { isLoading, error ->
        when {
            isLoading -> AssignmentDetailUiState.Loading
            error != null -> AssignmentDetailUiState.Error(error)
            _assignment != null -> AssignmentDetailUiState.Success(_assignment!!)
            else -> AssignmentDetailUiState.Loading
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AssignmentDetailUiState.Loading
    )

    fun loadAssignment(id: String) {
        currentAssignmentId = id
        viewModelScope.launch {
            val assignment = assignmentRepository.getAssignmentById(id)
            if (assignment != null) {
                _assignment = assignment
            }
        }
    }

    fun toggleCompletion(completed: Boolean) {
        currentAssignmentId?.let { id ->
            viewModelScope.launch {
                assignmentRepository.updateAssignmentCompletion(id, completed)
            }
        }
    }
}