package com.mytask.domain.usecase

import com.mytask.data.repositories.project.ProjectRepository
import com.mytask.domain.model.Project
import kotlinx.coroutines.flow.StateFlow

class GetProjectListUseCase(
    private val repository: ProjectRepository
) {
    operator fun invoke(): StateFlow<List<Project>> = repository.projects
}