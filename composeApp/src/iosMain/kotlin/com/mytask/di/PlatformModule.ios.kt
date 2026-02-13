package com.mytask.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.mytask.core.database.AppDatabase
import com.mytask.core.settings.AppSettings
import com.mytask.core.settings.IosAppSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.dsl.module
import platform.Foundation.NSHomeDirectory
import platform.Foundation.NSUserDefaults

actual fun platformModule() = module {
    // Database
    single {
        val dbPath = NSHomeDirectory() + "/study_plan.db"
        Room.databaseBuilder<AppDatabase>(
            name = dbPath
        )
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .fallbackToDestructiveMigration(true)
            .build()
    }

    // DAOs - MUST be registered before repositories that depend on them
    single { get<AppDatabase>().assignmentDao() }
    single { get<AppDatabase>().examDao() }
    single { get<AppDatabase>().projectDao() }
    single { get<AppDatabase>().appConfigDao() }

    // Settings
    single<AppSettings> { IosAppSettings(NSUserDefaults.standardUserDefaults) }
}