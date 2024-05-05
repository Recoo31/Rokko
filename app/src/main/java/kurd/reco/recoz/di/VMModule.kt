package kurd.reco.recoz.di

import kurd.reco.recoz.MainVM
import kurd.reco.recoz.plugin.PluginManager
import kurd.reco.recoz.view.homescreen.HomeScreenVM
import org.koin.dsl.module


val viewModelModule = module {
    single { PluginManager() }
    single { MainVM() }
    single { HomeScreenVM(get()) }
//    single { DetailScreenVM(get()) }
//    single { SearchScreenVM(get()) }
}