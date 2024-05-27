package kurd.reco.recoz.plugin

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Plugin::class], version = 1)
abstract class PluginDatabase : RoomDatabase() {
    abstract fun pluginDao(): PluginDao
}
