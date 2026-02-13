package com.mytask.di

import com.mytask.core.database.AppDatabase
import org.koin.dsl.module

val databaseModule = module {
    // DAOs are registered in platformModule after AppDatabase is created
}