package com.mytask.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.mytask.core.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

actual fun appDatabaseModule() = module {
    single {
        // Get iOS Documents directory (proper location for databases)
        val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null
        )
        val dbPath = requireNotNull(documentDirectory?.path) + "/study_plan.db"

        Room.databaseBuilder<AppDatabase>(name = dbPath)
            .setDriver(BundledSQLiteDriver())           // REQUIRED for KMP
            .setQueryCoroutineContext(Dispatchers.IO)   // REQUIRED for async
            .fallbackToDestructiveMigration(true)       // Dev convenience
            .build()
    }
}