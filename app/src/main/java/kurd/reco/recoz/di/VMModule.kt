package kurd.reco.recoz.di

import kurd.reco.recoz.MainVM
import kurd.reco.recoz.plugin.PluginManager
import kurd.reco.recoz.view.detailscreen.DetailScreenVM
import kurd.reco.recoz.view.homescreen.HomeScreenVM
import kurd.reco.recoz.view.settings.SettingsVM
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val viewModelModule = module {
    single { PluginManager(get()) }
    single { MainVM() }
    viewModel { HomeScreenVM(get()) }
    viewModel { DetailScreenVM(get()) }
    single { SettingsVM() }
//    single { SearchScreenVM(get()) }
}