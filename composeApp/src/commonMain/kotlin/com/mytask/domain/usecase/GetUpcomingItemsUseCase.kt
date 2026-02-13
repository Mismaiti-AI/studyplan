package com.mytask.domain.usecase

import com.mytask.data.repositories.assignment.AssignmentRepository
import com.mytask.data.repositories.exam.ExamRepository
import com.mytask.data.repositories.project.ProjectRepository
import com.mytask.domain.model.Assignment
import com.mytask.domain.model.Exam
import com.mytask.domain.model.Project
import kotlinx.coroutines.flow.combine
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class GetUpcomingItemsUseCase(
    private val assignmentRepository: AssignmentRepository,
    private val examRepository: ExamRepository,
    private val projectRepository: ProjectRepository
) {
    operator fun invoke() = combine(
        assignmentRepository.assignments,
        examRepository.exams,
        projectRepository.projects
    ) { assignments, exams, projects ->
        val now = Clock.System.now()
        val nextWeek = now + 7.days
        
        val upcomingAssignments = assignments.filter { 
            it.dueDate != null && it.dueDate >= now && it.dueDate <= nextWeek && !it.completed 
        }
        val upcomingExams = exams.filter { 
            it.examDate != null && it.examDate >= now && it.examDate <= nextWeek 
        }
        val upcomingProjects = projects.filter { 
            it.dueDate != null && it.dueDate >= now && it.dueDate <= nextWeek && !it.completed 
        }
        
        UpcomingItems(
            assignments = upcomingAssignments,
            exams = upcomingExams,
            projects = upcomingProjects
        )
    }
}

data class UpcomingItems(
    val assignments: List<Assignment>,
    val exams: List<Exam>,
    val projects: List<Project>
)