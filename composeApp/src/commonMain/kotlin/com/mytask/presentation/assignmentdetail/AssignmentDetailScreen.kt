package com.mytask.presentation.assignmentdetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignmentDetailScreen(
    assignmentId: String,
    onNavigateBack: () -> Unit,
    viewModel: AssignmentDetailViewModel = koinViewModel { parametersOf(assignmentId) }
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is AssignmentDetailUiState.Loading -> {
            LoadingIndicator()
        }
        is AssignmentDetailUiState.Error -> {
            ErrorMessage(
                message = state.message,
                onRetry = { viewModel.loadAssignment() }
            )
        }
        is AssignmentDetailUiState.Success -> {
            AssignmentDetailContent(
                assignment = state.assignment,
                onNavigateBack = onNavigateBack
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AssignmentDetailContent(
    assignment: Assignment,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Assignment Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = assignment.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Subject: ${assignment.subject}",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Due Date: ${assignment.dueDate?.toString()?.substring(0, 10) ?: "Not set"}",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Priority: ${assignment.priority}",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Description:",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = assignment.description,
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Status: ${if (assignment.completed) "Completed" else "Pending"}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}