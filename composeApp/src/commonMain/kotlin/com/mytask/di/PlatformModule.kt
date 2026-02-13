package com.mytask.di

import com.mytask.core.database.AppDatabase
import com.mytask.data.remote.service.SheetsApiService
import com.mytask.core.config.SheetsApiConfig
import io.ktor.client.HttpClient
import org.koin.core.module.Module
import org.koin.dsl.module

expect fun platformModule(): Module

val networkModule = module {
    single { HttpClient(get()) }
    single { SheetsApiConfig(get()) }
    single { SheetsApiService(get(), get()) }
}

val commonModule = module {
    single { AppDatabase.create(get()) }
}