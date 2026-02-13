package com.mytask.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.mytask.core.database.AppDatabase
import com.mytask.core.settings.AndroidAppSettings
import com.mytask.core.settings.AppSettings
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual fun platformModule() = module {
    // Database
    single {
        val appContext = androidContext().applicationContext
        val dbFile = appContext.getDatabasePath("study_plan.db")
        Room.databaseBuilder<AppDatabase>(
            context = appContext,
            name = dbFile.absolutePath
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
    single<AppSettings> { AndroidAppSettings(androidContext()) }
}