package kurd.reco.recoz.db.favorite

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorite_table")
    fun getAllFavorites(): List<Favorite>

    @Query("SELECT * FROM favorite_table WHERE id = :id")
    fun getFavoriteById(id: String): Favorite?

    @Insert
    fun insertFavorite(favorite: Favorite)

    @Query("DELETE FROM favorite_table WHERE id = :id")
    fun deleteFavoriteById(id: String)
}