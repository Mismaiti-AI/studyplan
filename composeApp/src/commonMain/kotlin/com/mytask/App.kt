package com.mytask

import androidx.compose.runtime.Composable
import com.mytask.di.appModule
import com.mytask.di.databaseModule
import com.mytask.di.networkModule
import com.mytask.di.platformModule
import com.mytask.navigation.NavigationHost
import com.mytask.presentation.theme.AppTheme
import org.koin.compose.KoinApplication
import org.koin.dsl.KoinAppDeclaration

@Composable
fun App(koinAppDeclaration: KoinAppDeclaration? = null) {
    KoinApplication(
        application = {
            modules(
                appModule,
                networkModule,
                databaseModule,
                platformModule()
            )
            koinAppDeclaration?.invoke(this)
        }
    ) {
        AppTheme {
            NavigationHost()
        }
    }
}