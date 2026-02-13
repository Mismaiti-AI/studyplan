package com.mytask.di

import com.mytask.data.local.dao.AppConfigDao
import com.mytask.data.local.dao.AssignmentDao
import com.mytask.data.local.dao.ExamDao
import com.mytask.data.local.dao.ProjectDao
import org.koin.core.module.Module
import org.koin.dsl.module

expect fun databaseModule(): Module

val commonDatabaseModule = module {
    // DAOs - extracted from AppDatabase
    single { get<AppDatabase>().assignmentDao() }
    single { get<AppDatabase>().examDao() }
    single { get<AppDatabase>().projectDao() }
    single { get<AppDatabase>().appConfigDao() }
}