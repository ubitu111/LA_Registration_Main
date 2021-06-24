package ru.kireev.mir.registrarlizaalert.di.modules

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.kireev.mir.registrarlizaalert.presentation.viewmodel.*

val viewModelModule = module {
    viewModel { MainViewModel(get()) }
    viewModel { VolunteersViewModel(get()) }
    viewModel { GroupsViewModel(get()) }
    viewModel { AddManuallyViewModel(get(), get()) }
    viewModel { BarCodeScannerViewModel(get()) }
}