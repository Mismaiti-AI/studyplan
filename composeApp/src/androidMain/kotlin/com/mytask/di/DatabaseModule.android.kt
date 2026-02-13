package com.mytask.di

import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.mytask.core.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual fun appDatabaseModule() = module {
    single {
        val appContext = androidContext().applicationContext
        val dbFile = appContext.getDatabasePath("study_plan.db")
        Room.databaseBuilder<AppDatabase>(
            context = appContext,
            name = dbFile.absolutePath
        )
            .setDriver(BundledSQLiteDriver())           // REQUIRED for KMP
            .setQueryCoroutineContext(Dispatchers.IO)   // REQUIRED for async
            .fallbackToDestructiveMigration(true)       // Dev convenience
            .build()
    }
}