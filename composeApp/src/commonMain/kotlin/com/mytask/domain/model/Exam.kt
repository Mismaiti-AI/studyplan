package com.mytask.domain.model

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
data class Exam(
    val id: String = "",
    val title: String = "",
    val subject: String = "",
    val examDate: Instant? = null,
    val description: String = "",
    val preparationStatus: Boolean = false
) {
    val isPast: Boolean
        get() = examDate != null && examDate < Instant.fromEpochMilliseconds(System.currentTimeMillis())
    
    val isUpcoming: Boolean
        get() = examDate != null && examDate >= Instant.fromEpochMilliseconds(System.currentTimeMillis())
    
    val daysUntilExam: Int
        get() = if (examDate != null) {
            ((examDate.toEpochMilliseconds() - System.currentTimeMillis()) / (24 * 60 * 60 * 1000)).toInt()
        } else {
            -1
        }
}