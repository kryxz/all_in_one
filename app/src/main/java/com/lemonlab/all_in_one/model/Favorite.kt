package com.lemonlab.all_in_one.model

import android.content.Context
import androidx.room.*
import com.lemonlab.all_in_one.items.Category


@Entity(tableName = "favorites")
class Favorite(
    @PrimaryKey @ColumnInfo(name = "category") val category: Category,
    @ColumnInfo(name = "index") val index: Int
)

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: Favorite)

    @Update
    suspend fun updateFavorite(favorite: Favorite)

    @Delete
    suspend fun deleteFavorite(favorite: Favorite)

    @Query("SELECT `index` FROM favorites WHERE category == :category")
    suspend fun getFavoritesByCategory(category: Category): List<Int>

    @Query("SELECT * FROM favorites")
    suspend fun getFavorites(): List<Favorite>

}


class CategoryConverter {
    @TypeConverter
    fun storedStringToCategory(value: String): Category {
        return Category.valueOf(value)
    }

    @TypeConverter
    fun categoryToStoredString(category: Category): String {
        return category.toString()
    }

}

// Annotates class to be a Room Database with a table (entity) of the Word class
@Database(entities = [Favorite::class], version = 1, exportSchema = false)
@TypeConverters(CategoryConverter::class)
public abstract class FavoritesRoomDatabase : RoomDatabase() {

    abstract fun FavoriteDao(): FavoriteDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: FavoritesRoomDatabase? = null

        fun getDatabase(context: Context): FavoritesRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FavoritesRoomDatabase::class.java,
                    "favorites"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}