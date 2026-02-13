package com.mytask.domain.usecase

import com.mytask.data.repositories.assignment.AssignmentRepository
import com.mytask.domain.model.Assignment
import kotlinx.coroutines.flow.StateFlow

class GetAssignmentListUseCase(
    private val repository: AssignmentRepository
) {
    operator fun invoke(): StateFlow<List<Assignment>> = repository.assignments
}