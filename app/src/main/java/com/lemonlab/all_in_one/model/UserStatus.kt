package com.lemonlab.all_in_one.model

import com.google.firebase.firestore.FirebaseFirestore
import com.lemonlab.all_in_one.R
import com.lemonlab.all_in_one.items.Category
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


    private fun update() {
        val ref = FirebaseFirestore.getInstance().collection("statuses")

        ref.document(this.statusID).set(this)
    }


    fun like(likeID: String) {
        if (likesIDs == null)
            likesIDs = ArrayList()

        if (likesIDs!!.contains(likeID)) likesIDs!!.remove(likeID)
        else likesIDs!!.add(likeID)

        update()

    }


    fun getLikesCount(): Int {
        if (likesIDs != null)
            likesIDs!!.size
        return 0
    }


    fun getReportsCount(): Int {
        if (reportsIDs != null)
            reportsIDs!!.size
        return 0
    }

    fun report(reportID: String) {
        if (reportsIDs == null)
            reportsIDs = ArrayList()


        reportsIDs!!.add(reportID)

        update()
    }
}