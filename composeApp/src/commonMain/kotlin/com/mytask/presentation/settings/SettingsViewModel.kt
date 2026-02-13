package com.mytask.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mytask.data.repositories.appconfig.AppConfigRepository
import com.mytask.domain.model.AppConfig
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Define UiState in the same file as the ViewModel
sealed interface SettingsUiState {
    data object Loading : SettingsUiState
    data class Success(val config: AppConfig?) : SettingsUiState
    data class Error(val message: String) : SettingsUiState
}

class SettingsViewModel(
    private val appConfigRepository: AppConfigRepository
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        appConfigRepository.config,
        appConfigRepository.isLoading,
        appConfigRepository.error
    ) { config, isLoading, error ->
        when {
            isLoading -> SettingsUiState.Loading
            error != null -> SettingsUiState.Error(error)
            else -> SettingsUiState.Success(config)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState.Loading
    )

    fun loadConfig() {
        viewModelScope.launch {
            appConfigRepository.loadConfig()
        }
    }
}