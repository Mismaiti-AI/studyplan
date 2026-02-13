package com.mytask.domain.usecase

import com.mytask.data.repositories.appconfig.AppConfigRepository
import com.mytask.domain.model.AppConfig

class GetCurrentSheetConfigUseCase(
    private val repository: AppConfigRepository
) {
    suspend operator fun invoke(): AppConfig? {
        return repository.getConfig()
    }
}