package com.lemonlab.all_in_one.model

import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

data class ForumPost(
    val title: String, val text: String, val userID: String,
    val timestamp: Date, var comments: ArrayList<Comment>?,
    var likesIDs: ArrayList<String>?, var dislikesIDs: ArrayList<String>?,
    var reports: Int, var reportIDs: ArrayList<String>?, var postID: String
) {

    private fun updatePost() {
        val postRef = FirebaseFirestore.getInstance().collection("posts").document(postID)
        postRef.get().addOnSuccessListener {
            if (it.data != null)
                postRef.set(this)
        }
    }


    fun report(userID: String) {
        if (reportIDs!!.contains(userID)) return
        reports += 1
        reportIDs!!.add(userID)
        updatePost()
    }

    fun like(userID: String) {
        when {
            this.dislikesIDs!!.contains(userID) -> {
                dislikesIDs!!.remove(userID)
                likesIDs!!.add(userID)
            }
            !this.likesIDs!!.contains(userID) -> {
                likesIDs!!.add(userID)
            }
        }
        updatePost()
    }

    fun disLike(userID: String) {
        when {
            this.likesIDs!!.contains(userID) -> {
                likesIDs!!.remove(userID)
                dislikesIDs!!.add(userID)
            }
            !this.dislikesIDs!!.contains(userID) -> {
                dislikesIDs!!.add(userID)
            }
        }
        updatePost()
    }
}

data class Comment(val text: String, val userID: String)
