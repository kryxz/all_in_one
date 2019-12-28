package com.lemonlab.all_in_one

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navController = Navigation.findNavController(this, R.id.navHost)
        setUpBottomNavigation()
        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
    }


    override fun onNavigateUp(): Boolean =
        navController.navigateUp()


    private fun setUpBottomNavigation() =
        bottom_nav.setupWithNavController(navController)


    override fun onSupportNavigateUp(): Boolean =
        navController.navigateUp()

}
