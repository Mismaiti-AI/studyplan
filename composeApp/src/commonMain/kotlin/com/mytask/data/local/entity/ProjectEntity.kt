package com.mytask.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey
    val id: String,
    
    @ColumnInfo(name = "title")
    val title: String,
    
    @ColumnInfo(name = "description")
    val description: String,
    
    @ColumnInfo(name = "start_date_millis")
    val startDateMillis: Long?,
    
    @ColumnInfo(name = "due_date_millis")
    val dueDateMillis: Long?,
    
    @ColumnInfo(name = "subject")
    val subject: String,
    
    @ColumnInfo(name = "progress")
    val progress: Int,
    
    @ColumnInfo(name = "completed")
    val completed: Boolean
)