package com.mytask.presentation.sheeturlconfig

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mytask.core.config.SheetsApiConfig
import com.mytask.data.repositories.appconfig.AppConfigRepository
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
                // Save the URL to the config
                sheetsApiConfig.scriptUrl = _currentUrl.value

                // Update the Google Sheets URL in the repository
                val success = appConfigRepository.updateGoogleSheetUrl(_currentUrl.value)

                if (success) {
                    _uiState.value = SheetUrlConfigUiState.Success("Configuration saved successfully!")
                } else {
                    _uiState.value = SheetUrlConfigUiState.Error("Failed to save configuration")
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