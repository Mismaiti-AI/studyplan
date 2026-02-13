package com.mytask.data.repositories.appconfig

import com.mytask.domain.model.AppConfig
import kotlinx.coroutines.flow.StateFlow

interface AppConfigRepository {
    // ═══════════════════════════════════════════════════════════════
    // SHARED STATE - Observable by ALL ViewModels
    // ═══════════════════════════════════════════════════════════════
    
    /** Current app config - shared across all screens */
    val config: StateFlow<AppConfig?>
    
    /** Loading state */
    val isLoading: StateFlow<Boolean>
    
    /** Error state */
    val error: StateFlow<String?>
    
    // ═══════════════════════════════════════════════════════════════
    // ACTIONS - Called by ViewModels to modify state
    // ═══════════════════════════════════════════════════════════════
    
    /** Load config from Google Sheets */
    suspend fun loadConfig(): AppConfig?
    
    /** Save config to Google Sheets */
    suspend fun saveConfig(config: AppConfig): Boolean
    
    /** Update Google Sheets URL */
    suspend fun updateGoogleSheetUrl(url: String): Boolean
    
    /** Clear error state */
    fun clearError()
}