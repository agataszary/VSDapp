package com.example.vsdapp

import android.app.Application
import com.example.vsdapp.core.PreferencesDataStore
import com.example.vsdapp.core.koin.RepositoryModule
import com.example.vsdapp.core.koin.ViewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module

class App: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(applicationContext)
            modules(
                listOf(
                    ViewModelModule.get,
                    RepositoryModule.get,
                    module { single { PreferencesDataStore(androidContext()) } }
                )
            )
        }
    }
}