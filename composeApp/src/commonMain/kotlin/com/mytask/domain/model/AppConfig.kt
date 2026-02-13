package com.mytask.domain.model

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
data class AppConfig(
    val id: String = "app_config_singleton",
    val googleSheetsUrl: String = "",
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null
)