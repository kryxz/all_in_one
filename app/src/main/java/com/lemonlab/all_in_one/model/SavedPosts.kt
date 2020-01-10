package com.lemonlab.all_in_one.model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*


@Entity(tableName = "saved_posts")
class SavedPost(
    @PrimaryKey @ColumnInfo(name = "postID") val postID: String
)

@Dao
interface SavedPostsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(savedPosts: SavedPost)

    @Update
    suspend fun updatePost(savedPosts: SavedPost)

    @Delete
    suspend fun deletePost(savedPosts: SavedPost)

    @Query("SELECT postID FROM saved_posts")
    fun getAllSavedPosts(): LiveData<List<String>>

    @Query("DELETE FROM saved_posts")
    suspend fun removeAll()

}


// Annotates class to be a Room Database with a table (entity) of the Word class
@Database(entities = [SavedPost::class], version = 1, exportSchema = false)
abstract class SavedPostsRoomDatabase : RoomDatabase() {

    abstract fun savedPostsDao(): SavedPostsDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: SavedPostsRoomDatabase? = null

        fun getDatabase(context: Context): SavedPostsRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SavedPostsRoomDatabase::class.java,
                    "saved_posts"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}