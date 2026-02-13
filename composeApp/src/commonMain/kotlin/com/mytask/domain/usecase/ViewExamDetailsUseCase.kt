package com.mytask.domain.usecase

import com.mytask.data.repositories.exam.ExamRepository
import com.mytask.domain.model.Exam

class ViewExamDetailsUseCase(
    private val repository: ExamRepository
) {
    suspend operator fun invoke(examId: String): Exam? {
        return repository.getExamById(examId)
    }
}