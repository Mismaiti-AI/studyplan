package com.mytask.navigation

import kotlinx.serialization.Serializable

@Serializable
object Dashboard

@Serializable
object Assignments

@Serializable
object Exams

@Serializable
object Projects

@Serializable
object Settings

@Serializable
object Config

@Serializable
data class AssignmentDetail(val assignmentId: String)

@Serializable
data class ExamDetail(val examId: String)

@Serializable
data class ProjectDetail(val projectId: String)

@Serializable
object SheetUrlConfig