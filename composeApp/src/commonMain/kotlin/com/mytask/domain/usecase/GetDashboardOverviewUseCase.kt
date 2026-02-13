package com.mytask.domain.usecase

import com.mytask.data.repositories.assignment.AssignmentRepository
import com.mytask.data.repositories.exam.ExamRepository
import com.mytask.data.repositories.project.ProjectRepository
import com.mytask.domain.model.Assignment
import com.mytask.domain.model.Exam
import com.mytask.domain.model.Project
import kotlinx.coroutines.flow.combine

class GetDashboardOverviewUseCase(
    private val assignmentRepository: AssignmentRepository,
    private val examRepository: ExamRepository,
    private val projectRepository: ProjectRepository
) {
    operator fun invoke() = combine(
        assignmentRepository.assignments,
        examRepository.exams,
        projectRepository.projects
    ) { assignments, exams, projects ->
        DashboardOverview(
            assignments = assignments,
            exams = exams,
            projects = projects
        )
    }
}

data class DashboardOverview(
    val assignments: List<Assignment>,
    val exams: List<Exam>,
    val projects: List<Project>
)