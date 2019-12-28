package com.lemonlab.all_in_one.extensions

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
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