package com.mytask.presentation.sheeturlconfig

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.autoMirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mytask.presentation.components.ErrorMessage
import com.mytask.presentation.components.LoadingIndicator
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SheetUrlConfigScreen(
    onNavigateBack: () -> Unit,
    onConfigurationComplete: () -> Unit,
    viewModel: SheetUrlConfigViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is SheetUrlConfigUiState.Loading -> {
            LoadingIndicator()
        }
        is SheetUrlConfigUiState.Error -> {
            ErrorMessage(
                message = state.message,
                onRetry = { /* Retry handled by user actions */ }
            )
        }
        is SheetUrlConfigUiState.Success -> {
            SheetUrlConfigContent(
                currentUrl = state.currentUrl,
                onSave = { url -> viewModel.saveSheetUrl(url, onConfigurationComplete) },
                onNavigateBack = onNavigateBack
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SheetUrlConfigContent(
    currentUrl: String,
    onSave: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    var url by remember { mutableStateOf(currentUrl) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configure Data Source") },
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
                text = "Google Sheets URL",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Enter the URL of your Google Sheets Apps Script endpoint to connect your data.",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                label = { Text("Google Sheets URL") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = { onSave(url) },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Save Configuration")
            }
        }
    }
}