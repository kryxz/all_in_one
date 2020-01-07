package com.lemonlab.all_in_one

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.lemonlab.all_in_one.model.UserStatus
import kotlinx.coroutines.launch

class FireStoreRepository {
    var db = FirebaseFirestore.getInstance()


    fun getStatusesRef(): CollectionReference {
        return db.collection("statuses")

    }


}

class UsersTextsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FireStoreRepository = FireStoreRepository()
    private val usersStatuses: MutableLiveData<List<UserStatus>> = MutableLiveData()
    private val statusLikes: MutableLiveData<Pair<Int, String>> = MutableLiveData()


    fun getStatuses(): MutableLiveData<List<UserStatus>> {
        viewModelScope.launch {
            repository.getStatusesRef().addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                if (snapshot == null) return@addSnapshotListener

                val documents = snapshot.documents
                val theStatuses: MutableList<UserStatus> = mutableListOf()
                for (item in documents)
                    theStatuses.add(item.toObject(UserStatus::class.java)!!)

                theStatuses.sortByDescending {
                    it.timestamp.time
                }

                usersStatuses.value = theStatuses
            }

        }
        return usersStatuses
    }

    fun likesCount(statusID: String): LiveData<Pair<Int, String>> {
        viewModelScope.launch {
            repository.getStatusesRef().document(statusID).addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                if (snapshot == null) return@addSnapshotListener

                val status = snapshot.toObject(UserStatus::class.java)

                statusLikes.value = Pair(status!!.likesCount(), snapshot.id)

            }
        }

        return statusLikes

    }


}