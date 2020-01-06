package com.lemonlab.all_in_one

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.lemonlab.all_in_one.model.Favorite
import com.lemonlab.all_in_one.model.FavoriteDao
import com.lemonlab.all_in_one.model.FavoritesRoomDatabase
import kotlinx.coroutines.launch

class FavoritesRepository(private val favoriteDao: FavoriteDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allFavorites: LiveData<List<Favorite>> = favoriteDao.getAllFavorites()
    var favoritesCodes: LiveData<List<Int>> = favoriteDao.getFavoritesCodes()

    suspend fun insert(favorite: Favorite) {
        favoriteDao.insertFavorite(favorite)
    }

    suspend fun delete(favorite: Favorite) {
        favoriteDao.deleteFavorite(favorite)
    }

    suspend fun update() {
        favoritesCodes = favoriteDao.getFavoritesCodes()
    }

}


// Class extends AndroidViewModel and requires application as a parameter.
class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    // The ViewModel maintains a reference to the repository to get data.
    private val repository: FavoritesRepository
    // LiveData gives us updated words when they change.
    val allFavorites: LiveData<List<Favorite>>
    val favoritesCodes: LiveData<List<Int>>

    init {
        // Gets reference to WordDao from WordRoomDatabase to construct
        // the correct WordRepository.
        val favoriteDao = FavoritesRoomDatabase.getDatabase(application).favoriteDao()
        repository = FavoritesRepository(favoriteDao)
        allFavorites = repository.allFavorites
        favoritesCodes = repository.favoritesCodes

    }

    /**
     * The implementation of insert() in the database is completely hidden from the UI.
     * Room ensures that you're not doing any long running operations on
     * the main thread, blocking the UI, so we don't need to handle changing Dispatchers.
     * ViewModels have a coroutine scope based on their lifecycle called
     * viewModelScope which we can use here.
     */
    fun insert(favorite: Favorite) = viewModelScope.launch {
        updateCodes()
        repository.insert(favorite)
    }


    fun remove(favorite: Favorite) = viewModelScope.launch {
        updateCodes()
        repository.delete(favorite)
    }

    fun updateCodes() =
        viewModelScope.launch {
            repository.update()
        }

}

