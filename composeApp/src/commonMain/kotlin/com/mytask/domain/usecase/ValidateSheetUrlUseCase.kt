package com.mytask.domain.usecase

import com.mytask.data.remote.service.SheetsApiService

class ValidateSheetUrlUseCase(
    private val sheetsApiService: SheetsApiService
) {
    suspend operator fun invoke(url: String): Result<Boolean> {
        return try {
            val isValid = sheetsApiService.ping()
            Result.success(isValid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}