package com.mytask.presentation.assignmentlist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mytask.domain.model.Assignment
import com.mytask.presentation.components.ErrorMessage
import com.mytask.presentation.components.LoadingIndicator
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignmentListScreen(
    onAssignmentClick: (String) -> Unit,
    onAddAssignment: () -> Unit,
    viewModel: AssignmentListViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is AssignmentListUiState.Loading -> {
            LoadingIndicator()
        }
        is AssignmentListUiState.Error -> {
            ErrorMessage(
                message = state.message,
                onRetry = { viewModel.loadAssignments() }
            )
        }
        is AssignmentListUiState.Success -> {
            AssignmentListContent(
                assignments = state.assignments,
                onAssignmentClick = onAssignmentClick,
                onAddAssignment = onAddAssignment
            )
        }
    }
}

@Composable
private fun AssignmentListContent(
    assignments: List<Assignment>,
    onAssignmentClick: (String) -> Unit,
    onAddAssignment: () -> Unit
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddAssignment
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Assignment")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
        ) {
            items(assignments) { assignment ->
                AssignmentItem(
                    assignment = assignment,
                    onClick = { onAssignmentClick(assignment.id) }
                )
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
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Priority: ${assignment.priority}",
                    style = MaterialTheme.typography.bodySmall
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                Text(
                    text = if (assignment.completed) "✓ Completed" else "○ Pending",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}