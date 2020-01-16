package com.lemonlab.all_in_one

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.lemonlab.all_in_one.model.*
import kotlinx.coroutines.launch


enum class SortBy {
    Time, Likes

}

class SavedPostsRepo(private val savedPostsDao: SavedPostsDao) {

    var savedPostsIDs: LiveData<List<String>> = savedPostsDao.getAllSavedPosts()

    suspend fun insert(savedPost: SavedPost) {
        savedPostsDao.insertPost(savedPost)
    }

    suspend fun remove(savedPost: SavedPost) {
        savedPostsDao.deletePost(savedPost)

    }

    fun getSavedPosts(): LiveData<List<String>> {
        return savedPostsDao.getAllSavedPosts()
    }

    suspend fun removeAll() {
        savedPostsDao.removeAll()
    }


}

class FireStoreRepository {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()


    fun getStatusesRef(): CollectionReference {
        return db.collection("statuses")

    }

    fun getPostsRef(): CollectionReference {
        return db.collection("posts")
    }


    fun getPostRef(id: String): DocumentReference {
        return db.collection("posts").document(id)
    }

    fun getUsersRef(): CollectionReference {
        return db.collection("users")

    }

    fun getUserID(): String {
        return auth.uid.toString()
    }


    fun getImagesRef(): CollectionReference {
        return db.collection("users_images")
    }

    fun getMessagesRef(): CollectionReference {
        return db.collection("chats")
    }

}

class UsersTextsViewModel(application: Application) : AndroidViewModel(application) {


    private val repository: FireStoreRepository

    private val postsRepo: SavedPostsRepo


    // users statuses/texts
    private val usersStatuses: MutableLiveData<List<UserStatus>>
    private val statusLikes: MutableLiveData<Pair<Int, String>>

    // users posts
    private val usersPosts: MutableLiveData<List<ForumPost>>
    private val savedUsersPosts: MutableLiveData<List<ForumPost>>
    var savedPostsIDs: LiveData<List<String>>

    private val viewPost: MutableLiveData<ForumPost>

    init {
        val postsDao = SavedPostsRoomDatabase.getDatabase(application).savedPostsDao()
        postsRepo = SavedPostsRepo(postsDao)
        savedPostsIDs = postsRepo.savedPostsIDs

        usersStatuses = MutableLiveData()
        statusLikes = MutableLiveData()
        usersPosts = MutableLiveData()
        savedUsersPosts = MutableLiveData()
        viewPost = MutableLiveData()
        repository = FireStoreRepository()
    }


    fun savePost(postID: String) =
        viewModelScope.launch {
            postsRepo.insert(SavedPost(postID))
            savedPostsIDs = postsRepo.getSavedPosts()
        }

    fun removePost(postID: String) =
        viewModelScope.launch {
            postsRepo.remove(SavedPost(postID))
            savedPostsIDs = postsRepo.getSavedPosts()
        }


    fun getPost(id: String): MutableLiveData<ForumPost> {
        repository.getPostRef(id).addSnapshotListener { snapshot, e ->
            if (e != null) return@addSnapshotListener
            if (snapshot == null) return@addSnapshotListener
            if (snapshot.data == null) return@addSnapshotListener
            viewPost.value = snapshot.toObject(ForumPost::class.java)

        }
        return viewPost
    }

    fun removeAllSavedPosts() {
        viewModelScope.launch {
            postsRepo.removeAll()
        }
    }

    fun getSavedPosts(): MutableLiveData<List<ForumPost>> {
        val posts: MutableList<ForumPost> = mutableListOf()
        for (id in ForumFragment.savedPosts) {
            repository.getPostRef(id).addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                if (snapshot == null) {
                    viewModelScope.launch {
                        postsRepo.remove(SavedPost(id))
                    }
                    return@addSnapshotListener
                }
                if (snapshot.data != null)
                    posts.add(snapshot.toObject(ForumPost::class.java)!!)
                savedUsersPosts.value = posts
            }
        }
        return savedUsersPosts
    }

    fun getPosts(): MutableLiveData<List<ForumPost>> {
        viewModelScope.launch {
            repository.getPostsRef().addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                if (snapshot == null) return@addSnapshotListener
                val documents = snapshot.documents
                val posts: MutableList<ForumPost> = mutableListOf()
                for (item in documents) {
                    if (item.data == null) continue
                    posts.add(item.toObject(ForumPost::class.java)!!)
                }

                posts.sortByDescending {
                    it.timestamp.time
                }
                usersPosts.value = posts

            }
        }
        return usersPosts

    }


    fun getStatuses(sortBy: SortBy): MutableLiveData<List<UserStatus>> {
        usersStatuses.value = mutableListOf()
        viewModelScope.launch {
            repository.getStatusesRef().addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                if (snapshot == null) return@addSnapshotListener

                val documents = snapshot.documents
                val theStatuses: MutableList<UserStatus> = mutableListOf()
                for (item in documents) {
                    if (item.data == null) continue
                    theStatuses.add(item.toObject(UserStatus::class.java)!!)
                }

                when (sortBy) {
                    SortBy.Time -> theStatuses.sortByDescending {
                        it.timestamp.time
                    }
                    SortBy.Likes -> theStatuses.sortByDescending {
                        it.likesCount()
                    }
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
                if (snapshot.data == null) return@addSnapshotListener

                val status = snapshot.toObject(UserStatus::class.java)!!
                val id = snapshot.id
                statusLikes.value = Pair(status.likesCount(), id)

            }
        }

        return statusLikes

    }

    fun getSenderName(userID: String): MutableLiveData<String> {
        val name: MutableLiveData<String> = MutableLiveData()


        repository.getUsersRef().document(userID).get().addOnSuccessListener {
            if (it == null || it.data == null) return@addOnSuccessListener
            name.value = it.data!!["name"].toString()

        }

        return name

    }


    fun getUserID(): String {
        return repository.getUserID()
    }

    fun getPostComments(postID: String): MutableLiveData<List<Comment>> {
        val comments = MutableLiveData<List<Comment>>()
        repository.getPostRef(postID).addSnapshotListener { snapshot, e ->
            if (e != null) return@addSnapshotListener
            if (snapshot == null) return@addSnapshotListener
            if (snapshot.data == null) return@addSnapshotListener

            comments.value = snapshot.toObject(ForumPost::class.java)!!.comments

            comments.value!!.sortedByDescending {
                it.timestamp.time
            }
            comments.value = comments.value!!.asReversed()

        }

        return comments
    }


}