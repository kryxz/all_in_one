package com.lemonlab.all_in_one.model

import java.util.*
import kotlin.collections.ArrayList

data class ForumPost(
    val title: String, val text: String, val userID: String,
    val timestamp: Date, var comments: ArrayList<Comment>?,
    var likesIDs: ArrayList<String>?, var dislikesIDs: ArrayList<String>?,
    var likes: Int, var dislikes: Int,
    var reports: Int, var reportIDs: ArrayList<String>?
)

data class Comment(val text: String, val userID: String)
