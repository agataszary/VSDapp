package com.example.vsdapp.core.koin

import com.example.vsdapp.editMode.EditModeRepository
import org.koin.core.module.Module
import org.koin.dsl.module

object RepositoryModule {
    val get : Module
        get() = module {
            factory { EditModeRepository() }
        }
}