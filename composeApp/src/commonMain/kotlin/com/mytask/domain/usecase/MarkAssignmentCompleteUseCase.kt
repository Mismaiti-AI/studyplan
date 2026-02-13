package com.mytask.domain.usecase

import com.mytask.data.repositories.assignment.AssignmentRepository
import com.mytask.domain.model.Assignment

class MarkAssignmentCompleteUseCase(
    private val repository: AssignmentRepository
) {
    suspend operator fun invoke(assignment: Assignment): Result<Unit> {
        return repository.updateAssignment(assignment.copy(completed = true))
    }
}