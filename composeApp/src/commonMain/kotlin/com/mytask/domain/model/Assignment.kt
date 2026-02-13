package com.mytask.domain.model

import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
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
        get() = dueDate != null && dueDate < Clock.System.now()

    val isDueSoon: Boolean
        get() {
            if (dueDate == null) return false
            val now = Clock.System.now()
            val twoDaysLater = now + 2.days
            return dueDate >= now && dueDate <= twoDaysLater
        }
}