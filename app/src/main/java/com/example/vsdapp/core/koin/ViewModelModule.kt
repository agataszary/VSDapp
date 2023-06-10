package com.example.vsdapp.core.koin

import com.example.vsdapp.editMode.EditModeViewModel
import com.example.vsdapp.gallery.GalleryViewModel
import com.example.vsdapp.readMode.ReadModeViewModel
import com.example.vsdapp.settings.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

object ViewModelModule {
    val get: Module
        get() = module {
            viewModel { EditModeViewModel(get()) }
            viewModel { GalleryViewModel() }
            viewModel { ReadModeViewModel() }
            viewModel { SettingsViewModel() }
        }
}