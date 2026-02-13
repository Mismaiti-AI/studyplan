package com.mytask.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Entity(tableName = "exams")
data class ExamEntity(
    @PrimaryKey
    val id: String,
    
    @ColumnInfo(name = "title")
    val title: String,
    
    @ColumnInfo(name = "subject")
    val subject: String,
    
    @ColumnInfo(name = "exam_date_millis")
    val examDateMillis: Long?,
    
    @ColumnInfo(name = "description")
    val description: String,
    
    @ColumnInfo(name = "preparation_status")
    val preparationStatus: Boolean
)