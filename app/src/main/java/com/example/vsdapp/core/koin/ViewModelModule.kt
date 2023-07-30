package com.example.vsdapp.core.koin

import com.example.vsdapp.editMode.EditModeViewModel
import com.example.vsdapp.gallery.GalleryViewModel
import com.example.vsdapp.login.LoginViewModel
import com.example.vsdapp.navigationMenu.NavigationViewModel
import com.example.vsdapp.readMode.ReadModeViewModel
import com.example.vsdapp.register.RegisterViewModel
import com.example.vsdapp.settings.SettingsViewModel
import com.example.vsdapp.students.StudentsViewModel
import com.example.vsdapp.studentsGallery.StudentsGalleryViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

object ViewModelModule {
    val get: Module
        get() = module {
            viewModel { EditModeViewModel(get(), get()) }
            viewModel { GalleryViewModel(get()) }
            viewModel { ReadModeViewModel(get()) }
            viewModel { SettingsViewModel(get()) }
            viewModel { NavigationViewModel(get()) }
            viewModel { LoginViewModel(get()) }
            viewModel { RegisterViewModel(get()) }
            viewModel { StudentsViewModel(get()) }
            viewModel { StudentsGalleryViewModel(get()) }
        }
}