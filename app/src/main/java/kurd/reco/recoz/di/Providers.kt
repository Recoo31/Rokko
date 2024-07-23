package kurd.reco.recoz.di

import android.app.Application
import androidx.room.Room
import kurd.reco.recoz.db.favorite.FavoriteDao
import kurd.reco.recoz.db.favorite.FavoriteDatabase
import kurd.reco.recoz.db.plugin.DeletedPluginDao
import kurd.reco.recoz.db.plugin.MIGRATION_1_2
import kurd.reco.recoz.db.plugin.PluginDao
import kurd.reco.recoz.db.plugin.PluginDatabase

fun provideDatabase(application: Application): PluginDatabase =
    Room.databaseBuilder(
        application,
        PluginDatabase::class.java,
        "plugin_database"
    ).allowMainThreadQueries().addMigrations(MIGRATION_1_2).build()

fun providePluginDao(pluginDataBase: PluginDatabase): PluginDao = pluginDataBase.pluginDao()

fun provideDeletedPluginDao(pluginDataBase: PluginDatabase): DeletedPluginDao = pluginDataBase.deletedPluginDao()
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

fun provideFavoriteDatabase(application: Application): FavoriteDatabase =
    Room.databaseBuilder(
        application,
        FavoriteDatabase::class.java,
        "favorite_database"
    ).allowMainThreadQueries().build()

fun provideFavoriteDao(favoriteDatabase: FavoriteDatabase): FavoriteDao = favoriteDatabase.favoriteDao()