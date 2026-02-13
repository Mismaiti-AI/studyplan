package com.mytask.di

import android.content.Context
import com.mytask.core.database.AppDatabase
import com.mytask.core.settings.AndroidAppSettings
import com.mytask.core.settings.AppSettings
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual fun platformModule() = module {
    // Database
    single { AppDatabase.create(androidContext()) }

    // DAOs - MUST be registered before repositories that depend on them
    single { get<AppDatabase>().assignmentDao() }
    single { get<AppDatabase>().examDao() }
    single { get<AppDatabase>().projectDao() }
    single { get<AppDatabase>().appConfigDao() }

    // Settings
    single<AppSettings> { AndroidAppSettings(androidContext()) }
}