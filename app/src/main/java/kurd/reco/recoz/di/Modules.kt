package kurd.reco.recoz.di

import android.app.Application
import androidx.room.Room
import kurd.reco.recoz.MainVM
import kurd.reco.recoz.plugin.DeletedPluginDao
import kurd.reco.recoz.plugin.MIGRATION_1_2
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
    ).allowMainThreadQueries().addMigrations(MIGRATION_1_2).build()

fun providePluginDao(pluginDataBase: PluginDatabase): PluginDao = pluginDataBase.pluginDao()

fun provideDeletedPluginDao(pluginDataBase: PluginDatabase): DeletedPluginDao = pluginDataBase.deletedPluginDao()

val dataBaseModule = module {
    single { provideDataBase(get()) }
    single { providePluginDao(get()) }
    single { provideDeletedPluginDao(get()) }
}

val viewModelModule = module {
    single { PluginManager(get(), get(), get()) }
    single { MainVM() }
    viewModel { HomeScreenVM(get()) }
    viewModel { DetailScreenVM(get()) }
    viewModel { SearchScreenVM(get()) }
    viewModel { SettingsVM(get(), get()) }
}