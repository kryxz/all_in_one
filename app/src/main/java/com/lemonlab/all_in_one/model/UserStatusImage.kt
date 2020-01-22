package com.lemonlab.all_in_one.model

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.sql.Timestamp
import java.util.*
import kotlin.collections.ArrayList

data class UserStatusImage(
    val url: String, var imageID: String,
    val timestamp: Date,
    val userId: String, val reportIDs: ArrayList<String>?
) {

    constructor() : this(
        "", "", Timestamp(System.currentTimeMillis()), "", ArrayList()
    )


    private fun checkReports() {
        if (reportIDs!!.size >= 5)
            deleteImage()
    }

    fun deleteImage() {
        FirebaseFirestore.getInstance().collection("users_images").document(imageID)
            .delete()
        FirebaseStorage.getInstance().reference.child("$imageID.png").delete()

    }

    fun report(userID: String) {
        if (reportIDs!!.contains(userID)) return
        reportIDs.add(userID)
        updatePost()
        checkReports()
    }

    private fun updatePost() {
        val ref = FirebaseFirestore.getInstance().collection("posts").document(imageID)
        ref.get().addOnSuccessListener {
            if (it.data != null)
                ref.set(this)
        }
    }


}