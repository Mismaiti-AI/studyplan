package com.mytask.domain.model

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
data class Assignment(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val dueDate: Instant? = null,
    val subject: String = "",
    val completed: Boolean = false,
    val priority: String = "medium"
) {
    val isOverdue: Boolean
        get() = dueDate != null && dueDate < Instant.fromEpochMilliseconds(System.currentTimeMillis())
    
    val isDueSoon: Boolean
        get() = dueDate != null && dueDate >= Instant.fromEpochMilliseconds(System.currentTimeMillis()) && 
                dueDate <= Instant.fromEpochMilliseconds(System.currentTimeMillis() + 2 * 24 * 60 * 60 * 1000) // Within 2 days
}