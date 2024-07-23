package kurd.reco.recoz.db.favorite

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_table")
data class Favorite(
    @PrimaryKey val id: String,
    val title: String,
    val image: String,
    val isSeries: Boolean,
    val pluginID: String
)
