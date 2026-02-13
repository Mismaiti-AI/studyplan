package com.mytask.presentation.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mytask.domain.model.Assignment
import com.mytask.domain.model.Exam
import com.mytask.domain.model.Project
import com.mytask.presentation.components.ErrorMessage
import com.mytask.presentation.components.LoadingIndicator
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onAssignmentClick: (String) -> Unit,
    onExamClick: (String) -> Unit,
    onProjectClick: (String) -> Unit,
    viewModel: DashboardViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is DashboardUiState.Loading -> {
            LoadingIndicator()
        }
        is DashboardUiState.Error -> {
            ErrorMessage(
                message = state.message,
                onRetry = { viewModel.loadDashboardData() }
            )
        }
        is DashboardUiState.Success -> {
            DashboardContent(
                upcomingAssignments = state.upcomingAssignments,
                upcomingExams = state.upcomingExams,
                upcomingProjects = state.upcomingProjects,
                onAssignmentClick = onAssignmentClick,
                onExamClick = onExamClick,
                onProjectClick = onProjectClick
            )
        }
    }
}

@Composable
private fun DashboardContent(
    upcomingAssignments: List<Assignment>,
    upcomingExams: List<Exam>,
    upcomingProjects: List<Project>,
    onAssignmentClick: (String) -> Unit,
    onExamClick: (String) -> Unit,
    onProjectClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "Upcoming Assignments",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (upcomingAssignments.isEmpty()) {
                Text(
                    text = "No upcoming assignments",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                upcomingAssignments.forEach { assignment ->
                    AssignmentItem(
                        assignment = assignment,
                        onClick = { onAssignmentClick(assignment.id) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
        
        item {
            Text(
                text = "Upcoming Exams",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (upcomingExams.isEmpty()) {
                Text(
                    text = "No upcoming exams",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                upcomingExams.forEach { exam ->
                    ExamItem(
                        exam = exam,
                        onClick = { onExamClick(exam.id) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
        
        item {
            Text(
                text = "Upcoming Projects",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (upcomingProjects.isEmpty()) {
                Text(
                    text = "No upcoming projects",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                upcomingProjects.forEach { project ->
                    ProjectItem(
                        project = project,
                        onClick = { onProjectClick(project.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AssignmentItem(
    assignment: Assignment,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = assignment.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "Subject: ${assignment.subject}",
                style = MaterialTheme.typography.bodySmall
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "Due: ${assignment.dueDate?.toString()?.substring(0, 10) ?: "Not set"}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun ExamItem(
    exam: Exam,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = exam.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "Subject: ${exam.subject}",
                style = MaterialTheme.typography.bodySmall
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "Date: ${exam.examDate?.toString()?.substring(0, 10) ?: "Not set"}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun ProjectItem(
    project: Project,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = project.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "Subject: ${project.subject}",
                style = MaterialTheme.typography.bodySmall
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "Due: ${project.dueDate?.toString()?.substring(0, 10) ?: "Not set"}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}