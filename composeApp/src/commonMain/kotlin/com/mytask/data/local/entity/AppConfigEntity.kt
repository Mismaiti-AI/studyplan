package com.mytask.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Entity(tableName = "app_configs")
data class AppConfigEntity(
    @PrimaryKey
    val id: String = "app_config_singleton",
    
    @ColumnInfo(name = "google_sheets_url")
    val googleSheetsUrl: String,
    
    @ColumnInfo(name = "created_at_millis")
    val createdAtMillis: Long?,
    
    @ColumnInfo(name = "updated_at_millis")
    val updatedAtMillis: Long?
)