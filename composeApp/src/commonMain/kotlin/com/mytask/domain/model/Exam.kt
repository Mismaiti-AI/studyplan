package com.mytask.domain.model

import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
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
        get() = examDate != null && examDate < Clock.System.now()

    val isUpcoming: Boolean
        get() = examDate != null && examDate >= Clock.System.now()

    val daysUntilExam: Int
        get() = if (examDate != null) {
            val now = Clock.System.now()
            val duration = examDate - now
            (duration.inWholeDays).toInt()
        } else {
            -1
        }
}