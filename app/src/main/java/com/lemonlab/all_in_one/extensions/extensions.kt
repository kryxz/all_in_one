package com.lemonlab.all_in_one.extensions

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.lemonlab.all_in_one.R


fun View.checkUser() {
    // tells user to login to continue.
    val currentUser = FirebaseAuth.getInstance().currentUser
    if (currentUser == null)
        this.findNavController().navigate(R.id.mustLoginFragment)
}

fun View.navigateToAndClear(destinationId:Int, newdes:Int){
    val navOptions = NavOptions.Builder().setPopUpTo(destinationId, true).build()
    this!!.findNavController().navigate(
        newdes,
        null, navOptions
    )
}

fun Context.showMessage(m:String){
    Toast.makeText(this, m,Toast.LENGTH_LONG).show()
}

fun View.makeTheUserOnline(){
    val uid = FirebaseAuth.getInstance().uid

    // check if there user logged in
    if(!uid.isNullOrEmpty()){
        val db = FirebaseFirestore.getInstance()

        db.collection("users").document("$uid")
            .update("online", "true")

        // store the data in the real time database
        FirebaseDatabase.getInstance().getReference("status/$uid").setValue("online")

        // Adding on disconnect hook
        FirebaseDatabase.getInstance().getReference("/status/$uid")
            .onDisconnect()     // Set up the disconnect hook
            .setValue("offline")
    }
}
