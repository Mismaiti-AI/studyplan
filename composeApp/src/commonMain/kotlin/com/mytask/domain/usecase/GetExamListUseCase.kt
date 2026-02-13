package com.mytask.domain.usecase

import com.mytask.data.repositories.exam.ExamRepository
import com.mytask.domain.model.Exam
import kotlinx.coroutines.flow.StateFlow

class GetExamListUseCase(
    private val repository: ExamRepository
) {
    operator fun invoke(): StateFlow<List<Exam>> = repository.exams
}