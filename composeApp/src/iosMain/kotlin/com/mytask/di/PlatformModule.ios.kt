package com.mytask.di

import com.mytask.core.database.AppDatabase
import com.mytask.core.settings.AppSettings
import com.mytask.core.settings.IosAppSettings
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults

actual fun platformModule() = module {
    // Database
    single { AppDatabase.create() }

    // DAOs - MUST be registered before repositories that depend on them
    single { get<AppDatabase>().assignmentDao() }
    single { get<AppDatabase>().examDao() }
    single { get<AppDatabase>().projectDao() }
    single { get<AppDatabase>().appConfigDao() }

    // Settings
    single<AppSettings> { IosAppSettings(NSUserDefaults.standardUserDefaults) }
}