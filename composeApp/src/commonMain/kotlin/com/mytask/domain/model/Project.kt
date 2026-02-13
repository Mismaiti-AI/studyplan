package com.mytask.domain.model

import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
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
        get() = !completed && dueDate != null && dueDate < Clock.System.now()

    val isWithinTwoDays: Boolean
        get() {
            if (dueDate == null) return false
            val now = Clock.System.now()
            val twoDaysLater = now + 2.days
            return dueDate >= now && dueDate <= twoDaysLater
        }

    val isCompletedOrPastDue: Boolean
        get() = completed || (dueDate != null && dueDate < Clock.System.now())
}