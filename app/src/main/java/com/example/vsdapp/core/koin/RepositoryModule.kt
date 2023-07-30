package com.example.vsdapp.core.koin

import com.example.vsdapp.database.StorageRepository
import com.example.vsdapp.editMode.EditModeRepository
import com.example.vsdapp.navigationMenu.NavigationRepository
import com.example.vsdapp.register.RegisterRepository
import com.example.vsdapp.settings.SettingsRepository
import com.example.vsdapp.students.StudentsRepository
import org.koin.core.module.Module
import org.koin.dsl.module

object RepositoryModule {
    val get : Module
        get() = module {
            factory { EditModeRepository() }
            factory { RegisterRepository() }
            factory { SettingsRepository() }
            factory { NavigationRepository() }
            factory { StorageRepository() }
            factory { StudentsRepository() }
        }
}