package kurd.reco.recoz.di

import kurd.reco.recoz.MainVM
import kurd.reco.recoz.plugin.PluginManager
import kurd.reco.recoz.view.detailscreen.DetailScreenVM
import kurd.reco.recoz.view.homescreen.HomeScreenVM
import kurd.reco.recoz.view.searchscreen.SearchScreenVM
import kurd.reco.recoz.view.settings.SettingsDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val dataBaseModule = module {
    single { provideDatabase(get()) }
    single { providePluginDao(get()) }
    single { provideDeletedPluginDao(get()) }

    single { provideFavoriteDatabase(get()) }
    single { provideFavoriteDao(get()) }

    single { SettingsDataStore(androidContext()) }
}

val viewModelModule = module {
    single { PluginManager(get(), get(), androidContext()) }
    single { MainVM(get(), get()) }
    single { HomeScreenVM(get()) }
    viewModel { DetailScreenVM(get()) }
    single { SearchScreenVM(get()) }
}