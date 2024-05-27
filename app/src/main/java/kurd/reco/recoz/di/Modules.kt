package kurd.reco.recoz.di

import android.app.Application
import androidx.room.Room
import kurd.reco.recoz.MainVM
import kurd.reco.recoz.plugin.PluginDao
import kurd.reco.recoz.plugin.PluginDatabase
import kurd.reco.recoz.plugin.PluginManager
import kurd.reco.recoz.view.detailscreen.DetailScreenVM
import kurd.reco.recoz.view.homescreen.HomeScreenVM
import kurd.reco.recoz.view.searchscreen.SearchScreenVM
import kurd.reco.recoz.view.settings.SettingsVM
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

fun provideDataBase(application: Application): PluginDatabase =
    Room.databaseBuilder(
        application,
        PluginDatabase::class.java,
        "plugin_database"
    ).allowMainThreadQueries().build()

fun provideDao(pluginDataBase: PluginDatabase): PluginDao = pluginDataBase.pluginDao()

val dataBaseModule = module {
    single { provideDataBase(get()) }
    single { provideDao(get()) }
}

val viewModelModule = module {
    single { PluginManager(get(), get()) }
    single { MainVM() }
    viewModel { HomeScreenVM(get()) }
    viewModel { DetailScreenVM(get()) }
    viewModel { SearchScreenVM(get()) }
    viewModel { SettingsVM(get()) }
}