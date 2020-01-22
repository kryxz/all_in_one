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
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.messaging.FirebaseMessaging
import com.lemonlab.all_in_one.extensions.hideKeypad
import com.lemonlab.all_in_one.items.Font
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    private lateinit var auth: FirebaseAuth
    private lateinit var fireStore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        setThemeAndFont()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navController = Navigation.findNavController(this, R.id.navHost)
        auth = FirebaseAuth.getInstance()
        fireStore = FirebaseFirestore.getInstance()

        setUpNavigation()

        checkIfFirstUse()
        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
    }


    private fun setThemeAndFont() {

        with(getSharedPreferences("UserPrefs", 0)) {
            val isDarkTheme = getBoolean("isDarkTheme", false)
            val font = Font.valueOf(getString("fontPref", Font.Cairo.toString())!!)
            if (isDarkTheme)
                setTheme(R.style.DarkAppTheme)
            else
                setTheme(R.style.LightAppTheme)

            val style = when (font) {
                Font.Cairo -> R.style.cairoFont
                Font.Mada -> R.style.madaFont
                Font.Taj -> R.style.tajFont
                Font.AlMar -> R.style.alMarFont
            }

            theme.applyStyle(style, false)
        }

    }

    private fun checkIfFirstUse() {
        // sets up fireBase and prefs if this is first use of app.
        val sharedPrefs = getSharedPreferences("UserPrefs", 0)
        val ifFirstUse = sharedPrefs.getBoolean("ifFirstUse", true)
        if (ifFirstUse) {
            setupFireBase()
            // todo: show prefs dialog
            sharedPrefs.edit().putBoolean("ifFirstUse", false).apply()
        }

    }

    private fun setupFireBase() {
        // set fireStore caching size to unlimited.
        fireStore.firestoreSettings = with(
            FirebaseFirestoreSettings.Builder()
        ) {
            isPersistenceEnabled = true
            cacheSizeBytes = FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED
            build()
        }

        NotificationSender().createNotificationChannel(this)

        // listen to notifications with MessagingService if user signed in.
        val uid = auth.uid
        with(FirebaseMessaging.getInstance()) {
            isAutoInitEnabled = true
            if (!uid.isNullOrEmpty())
                subscribeToTopic(uid)
        }

    }

    override fun onPause() {
        setOffline()
        super.onPause()
    }


    private fun setOffline() {
        // if there user logged in, make them offline.
        val uid = auth.uid
        if (!uid.isNullOrEmpty())
            fireStore.collection("users").document("$uid")
                .update("online", false)
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
        navController.addOnDestinationChangedListener { _, destination, _ ->
            hideKeypad()
            if (fragmentsWithoutAppBar.contains(destination.id))
                bottom_nav.visibility = View.INVISIBLE
            else
                bottom_nav.visibility = View.VISIBLE

        }


    }


    override fun onSupportNavigateUp(): Boolean =
        navController.navigateUp()

}
