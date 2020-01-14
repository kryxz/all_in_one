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
        val ref = FirebaseFirestore.getInstance()
            .collection("statuses").document(this.statusID)
        ref.set(this)
    }


    fun like(likeID: String) {
        val ref = FirebaseFirestore.getInstance()
            .collection("statuses").document(this.statusID)

        if (likesIDs == null)
            likesIDs = ArrayList()

        ref.get().addOnSuccessListener {
            if (it == null) return@addOnSuccessListener
            this.likesIDs = it.toObject(UserStatus::class.java)!!.likesIDs

            if (likesIDs!!.contains(likeID)) return@addOnSuccessListener

            likesIDs!!.add(likeID)
            update()
        }


    }

    fun cancelLike(likeID: String) {
        val ref = FirebaseFirestore.getInstance()
            .collection("statuses").document(this.statusID)

        if (likesIDs == null)
            likesIDs = ArrayList()

        ref.get().addOnSuccessListener {
            if (it == null) return@addOnSuccessListener
            this.likesIDs = it.toObject(UserStatus::class.java)!!.likesIDs

            if (!likesIDs!!.contains(likeID)) return@addOnSuccessListener

            likesIDs!!.remove(likeID)
            update()
        }


    }

    fun report(reportID: String) {
        val ref = FirebaseFirestore.getInstance()
            .collection("statuses").document(this.statusID)

        if (reportsIDs == null)
            reportsIDs = ArrayList()

        ref.get().addOnSuccessListener {
            if (it == null) return@addOnSuccessListener
            this.reportsIDs = it.toObject(UserStatus::class.java)!!.reportsIDs
            if (reportsIDs!!.contains(reportID)) return@addOnSuccessListener
            reportsIDs!!.add(reportID)
            update()
            checkReports()
        }
    }

    fun likesCount(): Int {
        if (likesIDs == null)
            return 0
        return likesIDs!!.size
    }


    private fun checkReports() {
        if (reportsIDs!!.size >= 5)
            delete()
    }

    private fun delete() {
        FirebaseFirestore.getInstance()
            .collection("statuses")
            .document(this.statusID).delete()

    }
}