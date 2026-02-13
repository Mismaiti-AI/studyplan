package com.mytask.presentation.sheeturlconfig

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mytask.core.config.SheetsApiConfig
import com.mytask.data.repositories.appconfig.AppConfigRepository
import com.mytask.domain.model.AppConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Define UiState in the same file as the ViewModel
sealed interface SheetUrlConfigUiState {
    data object Idle : SheetUrlConfigUiState
    data object Loading : SheetUrlConfigUiState
    data class Success(val message: String) : SheetUrlConfigUiState
    data class Error(val message: String) : SheetUrlConfigUiState
}

class SheetUrlConfigViewModel(
    private val sheetsApiConfig: SheetsApiConfig,
    private val appConfigRepository: AppConfigRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SheetUrlConfigUiState>(SheetUrlConfigUiState.Idle)
    val uiState: StateFlow<SheetUrlConfigUiState> = _uiState.asStateFlow()

    private val _currentUrl = MutableStateFlow(sheetsApiConfig.scriptUrl)
    val currentUrl: StateFlow<String> = _currentUrl.asStateFlow()

    fun updateUrl(newUrl: String) {
        _currentUrl.value = newUrl
    }

    fun saveUrl() {
        viewModelScope.launch {
            _uiState.value = SheetUrlConfigUiState.Loading
            
            try {
                // Validate the URL first
                val validationResult = appConfigRepository.validateSheetUrl(_currentUrl.value)
                
                if (validationResult is com.mytask.core.network.ApiResult.Success && validationResult.data) {
                    // Save the URL to the config
                    sheetsApiConfig.scriptUrl = _currentUrl.value
                    
                    // Update the app config in the repository
                    val config = AppConfig(
                        googleSheetsUrl = _currentUrl.value,
                        createdAt = null, // We'll let the repo handle timestamps
                        updatedAt = kotlin.time.Instant.fromEpochMilliseconds(System.currentTimeMillis())
                    )
                    
                    val result = appConfigRepository.updateConfig(config)
                    
                    if (result is com.mytask.core.network.ApiResult.Success) {
                        _uiState.value = SheetUrlConfigUiState.Success("Configuration saved successfully!")
                    } else {
                        _uiState.value = SheetUrlConfigUiState.Error("Failed to save configuration")
                    }
                } else {
                    _uiState.value = SheetUrlConfigUiState.Error("Invalid URL or connection failed")
                }
            } catch (e: Exception) {
                _uiState.value = SheetUrlConfigUiState.Error(e.message ?: "An error occurred")
            }
        }
    }

    fun validateUrl(): Boolean {
        return _currentUrl.value.isNotBlank() && _currentUrl.value.startsWith("https://")
    }
}