package com.mytask.domain.usecase

import com.mytask.data.repositories.appconfig.AppConfigRepository
import com.mytask.domain.model.AppConfig

class UpdateGoogleSheetUrlUseCase(
    private val repository: AppConfigRepository
) {
    suspend operator fun invoke(newUrl: String): Result<Unit> {
        return try {
            val success = repository.updateGoogleSheetUrl(newUrl)
            if (success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to update sheet URL"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}