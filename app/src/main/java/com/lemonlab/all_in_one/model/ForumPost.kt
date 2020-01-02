package com.lemonlab.all_in_one.model

import java.util.*

data class ForumPost(
    val title: String, val text: String, val userID: String,
    val timestamp: Date, var comments: ArrayList<Comment>?,
    var likesIDs: ArrayList<String>?, var dislikesIDs: ArrayList<String>?,
    var likes: Int, var dislikes: Int
)

data class Comment(val text: String, val userID: String)
