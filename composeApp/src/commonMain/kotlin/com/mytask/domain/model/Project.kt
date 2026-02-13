package com.mytask.domain.model

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
data class Project(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val startDate: Instant? = null,
    val dueDate: Instant? = null,
    val subject: String = "",
    val progress: Int = 0,
    val completed: Boolean = false
) {
    val isOverdue: Boolean
        get() = !completed && dueDate != null && dueDate < Instant.fromEpochMilliseconds(System.currentTimeMillis())
    
    val isWithinTwoDays: Boolean
        get() = dueDate != null && 
                dueDate >= Instant.fromEpochMilliseconds(System.currentTimeMillis()) &&
                dueDate <= Instant.fromEpochMilliseconds(System.currentTimeMillis() + 2 * 24 * 60 * 60 * 1000)
    
    val isCompletedOrPastDue: Boolean
        get() = completed || (dueDate != null && dueDate < Instant.fromEpochMilliseconds(System.currentTimeMillis()))
}