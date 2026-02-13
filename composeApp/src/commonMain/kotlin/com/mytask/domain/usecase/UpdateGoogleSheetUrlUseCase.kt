package com.mytask.domain.usecase

import com.mytask.data.repositories.appconfig.AppConfigRepository
import com.mytask.domain.model.AppConfig

class UpdateGoogleSheetUrlUseCase(
    private val repository: AppConfigRepository
) {
    suspend operator fun invoke(newUrl: String): Result<Unit> {
        val currentConfig = repository.getConfig()
        val updatedConfig = if (currentConfig != null) {
            currentConfig.copy(
                googleSheetsUrl = newUrl
            )
        } else {
            AppConfig(
                googleSheetsUrl = newUrl,
                createdAt = kotlin.time.Clock.System.now(),
                updatedAt = kotlin.time.Clock.System.now()
            )
        }
        
        return repository.saveConfig(updatedConfig)
    }
}