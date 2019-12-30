package com.lemonlab.all_in_one

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lemonlab.all_in_one.extensions.makeTheUserOnline
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navController = Navigation.findNavController(this, R.id.navHost)
        setUpNavigation()
        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
    }

    // TODO Implement back button for certain fragments(LocalQuotes, Profile, Settings, etc)


    override fun onPause() {
        val uid = FirebaseAuth.getInstance().uid

        // check if there user logged in
        if (!uid.isNullOrEmpty())
            FirebaseFirestore.getInstance().collection("users").document("$uid")
                .update("online", "false")
        super.onPause()
    }

    override fun onResume() {
        makeTheUserOnline()
        super.onResume()
    }

    override fun onNavigateUp(): Boolean =
        navController.navigateUp()


    private fun setUpNavigation() {
        bottom_nav.setupWithNavController(navController)

        // hide bottom nav if user is in register or login fragments.
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            if (destination.id == R.id.loginFragment || destination.id == R.id.registerFragment)
                bottom_nav.visibility = View.INVISIBLE
            else
                bottom_nav.visibility = View.VISIBLE

        }


    }


    override fun onSupportNavigateUp(): Boolean =
        navController.navigateUp()

}
