package com.mytask.data.repositories.appconfig

import com.mytask.core.network.ApiResult
import com.mytask.data.local.dao.AppConfigDao
import com.mytask.data.remote.service.SheetsApiService
import com.mytask.domain.model.AppConfig
import com.mytask.data.repositories.appconfig.AppConfigRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class AppConfigRepositoryImpl(
    private val sheetsApi: SheetsApiService,
    private val appConfigDao: AppConfigDao
) : AppConfigRepository {
    
    override val config: Flow<AppConfig?>
        get() = appConfigDao.getConfig().map { entity ->
            entity?.toDomain()
        }
    
    override val isLoading: Flow<Boolean>
        get() = kotlinx.coroutines.flow.flowOf(false) // Simplified for now
    
    override val error: Flow<String?>
        get() = kotlinx.coroutines.flow.flowOf(null) // Simplified for now
    
    override suspend fun loadConfig(): ApiResult<Unit> {
        return try {
            // In a real implementation, this would fetch from Google Sheets
            // For now, we'll just return success
            ApiResult.Success(Unit)
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }
    
    override suspend fun getConfig(): AppConfig? {
        return appConfigDao.getById("app_config_singleton")?.toDomain()
    }
    
    override suspend fun updateConfig(config: AppConfig): ApiResult<AppConfig> {
        return try {
            val entity = config.toEntity()
            appConfigDao.upsert(entity)
            ApiResult.Success(config)
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }
    
    override suspend fun validateSheetUrl(url: String): ApiResult<Boolean> {
        return try {
            // In a real implementation, this would ping the Google Sheets API
            // For now, we'll just return success if URL is not empty
            if (url.isNotBlank()) {
                ApiResult.Success(true)
            } else {
                ApiResult.Success(false)
            }
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }
}

// Extension functions for entity/domain conversion
@OptIn(ExperimentalTime::class)
fun AppConfigEntity.toDomain(): AppConfig {
    return AppConfig(
        id = id,
        googleSheetsUrl = googleSheetsUrl,
        createdAt = createdAtMillis?.let { Instant.fromEpochMilliseconds(it) },
        updatedAt = updatedAtMillis?.let { Instant.fromEpochMilliseconds(it) }
    )
}

@OptIn(ExperimentalTime::class)
fun AppConfig.toEntity(): AppConfigEntity {
    return AppConfigEntity(
        id = id,
        googleSheetsUrl = googleSheetsUrl,
        createdAtMillis = createdAt?.toEpochMilliseconds(),
        updatedAtMillis = updatedAt?.toEpochMilliseconds()
    )
}