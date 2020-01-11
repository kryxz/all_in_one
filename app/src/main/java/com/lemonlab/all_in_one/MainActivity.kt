package com.lemonlab.all_in_one

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lemonlab.all_in_one.extensions.makeTheUserOnline
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        setDarkOrLight()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navController = Navigation.findNavController(this, R.id.navHost)
        setUpNavigation()

        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
    }


    private fun setDarkOrLight() {
        val sharedPrefs = getSharedPreferences("UserPrefs", 0)
        val isDarkTheme = sharedPrefs.getBoolean("isDarkTheme", false)
        if (isDarkTheme)
            setTheme(R.style.DarkAppTheme)
        else
            setTheme(R.style.LightAppTheme)
    }

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

        val fragmentsWithNoBackButton = setOf(
            R.id.mainFragment,
            R.id.sendFragment,
            R.id.forumFragment,
            R.id.createFragment,
            R.id.chatFragment,
            R.id.mustLoginFragment
        )

        NavigationUI.setupActionBarWithNavController(
            this, navController,
            AppBarConfiguration.Builder(fragmentsWithNoBackButton).build()
        )

        val fragmentsWithoutAppBar = listOf(
            R.id.loginFragment, R.id.registerFragment, R.id.viewImageFragment
        )
        // hide bottom nav if user is in register or login fragments.
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            if (fragmentsWithoutAppBar.contains(destination.id))
                bottom_nav.visibility = View.INVISIBLE
            else
                bottom_nav.visibility = View.VISIBLE

        }


    }


    override fun onSupportNavigateUp(): Boolean =
        navController.navigateUp()

}
