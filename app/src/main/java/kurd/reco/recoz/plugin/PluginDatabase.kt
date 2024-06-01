package kurd.reco.recoz.plugin

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS `deleted_plugins` (`id` TEXT NOT NULL, PRIMARY KEY(`id`))")
    }
}


@Database(entities = [Plugin::class, DeletedPlugin::class], version = 2, exportSchema = false)
abstract class PluginDatabase : RoomDatabase() {
    abstract fun pluginDao(): PluginDao
    abstract fun deletedPluginDao(): DeletedPluginDao
}
