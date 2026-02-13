package com.mytask.domain.usecase

import com.mytask.data.repositories.project.ProjectRepository
import com.mytask.domain.model.Project

class ViewProjectDetailsUseCase(
    private val repository: ProjectRepository
) {
    suspend operator fun invoke(projectId: String): Project? {
        return repository.getProjectById(projectId)
    }
}