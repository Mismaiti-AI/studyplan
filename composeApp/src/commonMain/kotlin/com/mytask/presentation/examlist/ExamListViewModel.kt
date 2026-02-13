package com.mytask.presentation.examlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mytask.data.repositories.exam.ExamRepository
import com.mytask.domain.model.Exam
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Define UiState in the same file as the ViewModel
sealed interface ExamListUiState {
    data object Loading : ExamListUiState
    data class Success(val exams: List<Exam>) : ExamListUiState
    data class Error(val message: String) : ExamListUiState
}

class ExamListViewModel(
    private val examRepository: ExamRepository
) : ViewModel() {

    val uiState: StateFlow<ExamListUiState> = combine(
        examRepository.exams,
        examRepository.isLoading,
        examRepository.error
    ) { exams, isLoading, error ->
        when {
            isLoading -> ExamListUiState.Loading
            error != null -> ExamListUiState.Error(error)
            else -> ExamListUiState.Success(exams)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ExamListUiState.Loading
    )

    fun loadExams() {
        viewModelScope.launch {
            examRepository.loadExams()
        }
    }

    fun updatePreparationStatus(id: String, status: Boolean) {
        viewModelScope.launch {
            examRepository.updateExamPreparationStatus(id, status)
        }
    }
}