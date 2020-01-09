package com.lemonlab.all_in_one.model

import com.google.firebase.firestore.FirebaseFirestore
import java.sql.Timestamp
import java.util.*
import kotlin.collections.ArrayList

data class ForumPost(
    val title: String, val text: String, val userID: String,
    val timestamp: Date, var comments: ArrayList<Comment>?,
    var likesIDs: ArrayList<String>?, var dislikesIDs: ArrayList<String>?,
    var reportIDs: ArrayList<String>?, var postID: String
) {

    constructor() : this(
        "", "", "",
        Timestamp(System.currentTimeMillis()), ArrayList<Comment>(), ArrayList(),
        ArrayList(), ArrayList(), ""
    )


    private fun updatePost() {
        FirebaseFirestore.getInstance().collection("posts").document(postID).set(this)
    }


    fun sendComment(comment: Comment) {
        if (comments == null)
            comments = ArrayList()
        comments!!.add(comment)
        updatePost()
    }

    fun report(userID: String) {
        if (reportIDs!!.contains(userID)) return
        reportIDs!!.add(userID)
        updatePost()
        checkReports()
    }

    fun dislikesCount(): Int {
        return dislikesIDs!!.size
    }

    fun likesCount(): Int {
        return likesIDs!!.size
    }

    fun checkReports() {
        if (reportIDs!!.size >= 5)
            deletePost()
    }

    fun deletePost() {
        FirebaseFirestore.getInstance().collection("posts").document(postID)
            .delete()
    }

    fun likedBy(id: String): Boolean {
        return likesIDs!!.contains(id)
    }

    fun dislikedBy(id: String): Boolean {
        return dislikesIDs!!.contains(id)

    }

    fun like(userID: String) {
        when {
            likesIDs!!.contains(userID) -> likesIDs!!.remove(userID)
            dislikesIDs!!.contains(userID) -> {
                dislikesIDs!!.remove(userID)
                likesIDs!!.add(userID)
            }
            else -> likesIDs!!.add(userID)
        }

        updatePost()
    }

    fun disLike(userID: String) {

        when {
            dislikesIDs!!.contains(userID) -> dislikesIDs!!.remove(userID)
            likesIDs!!.contains(userID) -> {
                likesIDs!!.remove(userID)
                dislikesIDs!!.add(userID)
            }
            else -> dislikesIDs!!.add(userID)
        }

        updatePost()
    }
}

data class Comment(
    val text: String,
    val userID: String,
    val postID: String,
    val timestamp: Date,
    var reportIDs: ArrayList<String>?
) {

    constructor() : this(
        "", "", "",
        Timestamp(System.currentTimeMillis()), ArrayList<String>()
    )


    fun report(userID: String) {
        if (reportIDs == null)
            reportIDs = ArrayList()
        if (reportIDs!!.contains(userID)) return
        reportIDs!!.add(userID)
        updatePost()
        checkReports()
    }

    private fun checkReports() {
        if (reportIDs!!.size >= 5)
            deleteComment()
    }

    private fun updatePost() {
        val postRef = FirebaseFirestore.getInstance().collection("posts").document(postID)
        postRef.get().addOnSuccessListener {
            if (it == null) return@addOnSuccessListener
            val post = it.toObject(ForumPost::class.java)!!
            for (comment in post.comments!!) {
                if (comment.text == this.text)
                    comment.reportIDs = reportIDs
            }
            postRef.set(post)
        }
    }

    fun deleteComment() {
        val postRef = FirebaseFirestore.getInstance().collection("posts").document(postID)
        postRef.get().addOnSuccessListener {
            if (it == null) return@addOnSuccessListener
            val theComments = it.toObject(ForumPost::class.java)!!.comments
            val iterator = theComments!!.iterator()
            while (iterator.hasNext()) {
                val comment = iterator.next()
                if (comment.text == this.text)
                    iterator.remove()
            }
            postRef.update("comments", theComments)
        }
    }

}
