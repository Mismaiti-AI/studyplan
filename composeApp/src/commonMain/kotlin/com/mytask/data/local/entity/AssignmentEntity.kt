package com.mytask.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Entity(tableName = "assignments")
data class AssignmentEntity(
    @PrimaryKey
    val id: String,
    
    @ColumnInfo(name = "title")
    val title: String,
    
    @ColumnInfo(name = "description")
    val description: String,
    
    @ColumnInfo(name = "due_date_millis")
    val dueDateMillis: Long?,
    
    @ColumnInfo(name = "subject")
    val subject: String,
    
    @ColumnInfo(name = "completed")
    val completed: Boolean,
    
    @ColumnInfo(name = "priority")
    val priority: String
)