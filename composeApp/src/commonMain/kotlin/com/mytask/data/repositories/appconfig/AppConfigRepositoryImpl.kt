package com.mytask.data.repositories.appconfig

import com.mytask.data.local.dao.AppConfigDao
import com.mytask.data.local.entity.AppConfigEntity
import com.mytask.data.remote.service.SheetsApiService
import com.mytask.domain.model.AppConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class AppConfigRepositoryImpl(
    private val sheetsApi: SheetsApiService,
    private val appConfigDao: AppConfigDao
) : AppConfigRepository {

    private val repositoryScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    // ═══════════════════════════════════════════════════════════════
    // STATE MANAGEMENT - Repository holds state
    // ═══════════════════════════════════════════════════════════════

    override val config: StateFlow<AppConfig?> = appConfigDao.getConfig()
        .map { entity -> entity?.toDomain() }
        .stateIn(repositoryScope, SharingStarted.Eagerly, null)

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    override val error: StateFlow<String?> = _error.asStateFlow()

    // ═══════════════════════════════════════════════════════════════
    // ACTIONS
    // ═══════════════════════════════════════════════════════════════

    override suspend fun loadConfig(): AppConfig? {
        _isLoading.value = true
        _error.value = null
        return try {
            // In a real implementation, this would fetch from Google Sheets
            // For now, we'll just return the locally stored config
            val entity = appConfigDao.getById("app_config_singleton")
            entity?.toDomain()
        } catch (e: Exception) {
            _error.value = e.message ?: "Unknown error loading config"
            null
        } finally {
            _isLoading.value = false
        }
    }

    override suspend fun saveConfig(config: AppConfig): Boolean {
        _isLoading.value = true
        _error.value = null
        return try {
            val entity = config.toEntity()
            appConfigDao.upsert(entity)
            true
        } catch (e: Exception) {
            _error.value = e.message ?: "Unknown error saving config"
            false
        } finally {
            _isLoading.value = false
        }
    }

    override suspend fun updateGoogleSheetUrl(url: String): Boolean {
        _isLoading.value = true
        _error.value = null
        return try {
            val currentConfig = appConfigDao.getById("app_config_singleton")?.toDomain()
                ?: AppConfig(googleSheetsUrl = url)

            val updatedConfig = currentConfig.copy(
                googleSheetsUrl = url,
                updatedAt = kotlin.time.Clock.System.now()
            )

            val entity = updatedConfig.toEntity()
            appConfigDao.upsert(entity)
            true
        } catch (e: Exception) {
            _error.value = e.message ?: "Unknown error updating Google Sheet URL"
            false
        } finally {
            _isLoading.value = false
        }
    }

    override fun clearError() {
        _error.value = null
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
