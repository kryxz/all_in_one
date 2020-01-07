package com.lemonlab.all_in_one.model

import com.google.firebase.firestore.FirebaseFirestore
import com.lemonlab.all_in_one.R
import com.lemonlab.all_in_one.items.Category
import java.sql.Timestamp
import java.util.*
import kotlin.collections.ArrayList

enum class StatusColor(val value: Int) {
    Blue(R.color.darkBlue),
    Red(R.color.magentaPurple),
    Green(R.color.darkGreen),
    Black(R.color.superBlack)
}

data class UserStatus(
    val userID: String, val text: String, val category: Category,
    val statusColor: StatusColor, var statusID: String = "",
    var likesIDs: ArrayList<String>? = null, var reportsIDs: ArrayList<String>? = null,
    val timestamp: Date
) {

    constructor() : this(
        "", "", Category.Other, StatusColor.Blue,
        "", ArrayList<String>(), ArrayList<String>(), Timestamp(System.currentTimeMillis())
    )

    private fun update() {
        val ref = FirebaseFirestore.getInstance().collection("statuses")

        ref.document(this.statusID).set(this)
    }


    fun like(likeID: String) {
        if (likesIDs == null)
            likesIDs = ArrayList()

        if (likesIDs!!.contains(likeID)) return

        likesIDs!!.add(likeID)

        update()

    }

    fun cancelLike(likeID: String) {
        if (likesIDs == null)
            likesIDs = ArrayList()

        if (likesIDs!!.contains(likeID))
            likesIDs!!.remove(likeID)
        update()

    }


    fun likesCount(): Int {
        if (likesIDs == null)
            return 0
        return likesIDs!!.size
    }


    fun reportsCount(): Int {
        if (reportsIDs == null)
            return 0
        return reportsIDs!!.size
    }

    fun report(reportID: String) {
        if (reportsIDs == null)
            reportsIDs = ArrayList()


        reportsIDs!!.add(reportID)

        update()
    }
}