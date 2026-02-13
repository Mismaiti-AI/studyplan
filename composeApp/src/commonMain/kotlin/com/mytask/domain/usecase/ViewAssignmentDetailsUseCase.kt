package com.mytask.domain.usecase

import com.mytask.data.repositories.assignment.AssignmentRepository
import com.mytask.domain.model.Assignment

class ViewAssignmentDetailsUseCase(
    private val repository: AssignmentRepository
) {
    suspend operator fun invoke(assignmentId: String): Assignment? {
        return repository.getAssignmentById(assignmentId)
    }
}