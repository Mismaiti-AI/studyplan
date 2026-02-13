package com.mytask.domain.usecase

import com.mytask.data.repositories.assignment.AssignmentRepository
import com.mytask.domain.model.Assignment

class MarkAssignmentCompleteUseCase(
    private val repository: AssignmentRepository
) {
    suspend operator fun invoke(assignment: Assignment): Result<Unit> {
        return try {
            val updated = repository.updateAssignment(assignment.copy(completed = true))
            if (updated != null) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to update assignment"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}