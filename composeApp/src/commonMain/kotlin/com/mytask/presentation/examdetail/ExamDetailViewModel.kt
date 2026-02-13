package com.mytask.presentation.examdetail

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
sealed interface ExamDetailUiState {
    data object Loading : ExamDetailUiState
    data class Success(val exam: Exam) : ExamDetailUiState
    data class Error(val message: String) : ExamDetailUiState
}

class ExamDetailViewModel(
    private val examId: String,
    private val examRepository: ExamRepository
) : ViewModel() {

    private var _exam: Exam? = null

    init {
        loadExam(examId)
    }

    val uiState: StateFlow<ExamDetailUiState> = combine(
        examRepository.isLoading,
        examRepository.error
    ) { isLoading, error ->
        when {
            isLoading -> ExamDetailUiState.Loading
            error != null -> ExamDetailUiState.Error(error)
            _exam != null -> ExamDetailUiState.Success(_exam!!)
            else -> ExamDetailUiState.Loading
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ExamDetailUiState.Loading
    )

    fun loadExam(id: String) {
        viewModelScope.launch {
            val exam = examRepository.getExamById(id)
            if (exam != null) {
                _exam = exam
            }
        }
    }

    fun updatePreparationStatus(status: Boolean) {
        viewModelScope.launch {
            examRepository.toggleExamPreparationStatus(examId)
        }
    }
}