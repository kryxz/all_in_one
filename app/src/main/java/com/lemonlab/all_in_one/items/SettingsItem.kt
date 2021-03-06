package com.lemonlab.all_in_one.items

import android.app.Activity
import android.app.AlertDialog
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.net.Uri
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.lemonlab.all_in_one.MainActivity
import com.lemonlab.all_in_one.R
import com.lemonlab.all_in_one.extensions.recreateFragment
import com.lemonlab.all_in_one.extensions.showMessage
import com.lemonlab.all_in_one.extensions.showYesNoDialog
import com.lemonlab.all_in_one.model.StatusColor
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.change_font_dialog.view.*
import kotlinx.android.synthetic.main.change_name_dialog.view.*
import kotlinx.android.synthetic.main.main_color_pref_dialog.view.*
import kotlinx.android.synthetic.main.settings_item_text.view.*

enum class Option(val textID: Int) {
    ClearCache(R.string.cache_delete),
    SignOut(R.string.signOut),
    DarkTheme(R.string.darkMode),
    MoreApps(R.string.moreApps),
    PrivacyPolicy(R.string.privacyPolicyTitleAr),
    FAQ(R.string.faq),
    MainColor(R.string.mainColor),
    FontChange(R.string.fontChange),
    ChangeName(R.string.changeName),
    ShowImages(R.string.showImages),
    Notifications(R.string.notifications),

}

enum class Font(val styleID: Int, val fontID: Int) {
    Cairo(R.style.cairoFont, R.font.cairo_regular),
    Mada(R.style.madaFont, R.font.mada),
    Taj(R.style.tajFont, R.font.tajawal_regular),
    AlMar(R.style.alMarFont, R.font.almarai)
}

class SettingsItem(
    private val option: Option, private val activity: Activity
) :
    Item<ViewHolder>() {

    override fun getLayout() = R.layout.settings_item_text


    override fun bind(viewHolder: ViewHolder, position: Int) {
        val view = viewHolder.itemView
        val textView = view.settingsTextView
        val switch = view.settingsSwitch
        val context = view.context
        switch.visibility = View.GONE
        fun setIcon(icon: Int) {
            val drawable = ContextCompat.getDrawable(context, icon)!!
            textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
        }


        textView.text = context.getString(option.textID)

        when (option) {
            // Font change

            Option.FontChange -> {
                setIcon(R.drawable.ic_fonts)
                textView.setOnClickListener { changeFontDialog(it) }
            }


            // change name
            Option.ChangeName -> {
                setIcon(R.drawable.ic_account_circle)
                textView.setOnClickListener { changeNameDialog(it) }
            }

            // clear cache
            Option.ClearCache -> {
                setIcon(R.drawable.ic_clear_all)
                textView.setOnClickListener { clearCache(it) }
            }

            // app main color
            Option.MainColor -> {
                setIcon(R.drawable.ic_check_circle)
                tintDrawable(textView)
                textView.setOnClickListener { colorDialog(it) }
            }

            // sign out if signed in. else register
            Option.SignOut -> {
                val isSignedIn = FirebaseAuth.getInstance().currentUser != null
                // show logout
                if (isSignedIn) {
                    setIcon(R.drawable.ic_logout)
                    textView.setOnClickListener { signOut(it) }
                }
                // show register
                else {
                    setIcon(R.drawable.ic_expand_more)
                    textView.text = context.getString(R.string.loginOrRegister)
                    textView.setOnClickListener { registerNow(it) }
                }

            }

            // dark theme switch
            Option.DarkTheme -> {
                val sharedPrefs = context.getSharedPreferences("UserPrefs", 0)
                switch.isChecked = sharedPrefs.getBoolean("isDarkTheme", false)
                switch.visibility = View.VISIBLE
                switch.setOnCheckedChangeListener { _, isChecked ->
                    changeTheme(sharedPrefs, isChecked)
                }

            }
            // notifications switch
            Option.Notifications -> {
                val sharedPrefs = context.getSharedPreferences("UserPrefs", 0)
                switch.isChecked = sharedPrefs.getBoolean("notifications", true)
                switch.visibility = View.VISIBLE
                switch.setOnCheckedChangeListener { _, isChecked ->
                    changeNotifyPref(sharedPrefs, isChecked)
                }

            }
            // show Images in categories switch
            Option.ShowImages -> {
                val sharedPrefs = context.getSharedPreferences("UserPrefs", 0)
                switch.isChecked = sharedPrefs.getBoolean("showImages", true)
                switch.visibility = View.VISIBLE
                switch.setOnCheckedChangeListener { _, isChecked ->
                    showImages(sharedPrefs, isChecked)
                }

            }
            // more apps
            Option.MoreApps -> {
                setIcon(R.drawable.ic_apps)
                textView.setOnClickListener { moreApps(it) }
            }

            // privacy policy dialog
            Option.PrivacyPolicy -> {
                setIcon(R.drawable.ic_info)
                textView.setOnClickListener { showPrivacyPolicy(it) }
            }

            // FAQ fragment
            Option.FAQ -> {
                setIcon(R.drawable.ic_question_answer)
                textView.setOnClickListener { faq(it) }
            }
        }

    }

    private fun showImages(sharedPrefs: SharedPreferences, checked: Boolean) =
        sharedPrefs.edit().putBoolean("showImages", checked).apply()


    private fun changeNotifyPref(sharedPrefs: SharedPreferences, checked: Boolean) {
        val uid = FirebaseAuth.getInstance().uid!!
        with(FirebaseMessaging.getInstance()) {
            if (checked)
                subscribeToTopic(uid)
            else
                unsubscribeFromTopic(uid)
        }

        sharedPrefs.edit().putBoolean("notifications", checked).apply()

    }


    private fun changeNameDialog(textView: View) {
        val context = textView.context

        val dialogBuilder = AlertDialog.Builder(context).create()
        val dialogView = with(LayoutInflater.from(context)) {
            inflate(
                R.layout.change_name_dialog,
                null
            )
        }
        val newName = dialogView.new_name_editText
        val confirmButton = dialogView.dialogConfirmButton
        val cancelButton = dialogView.dialogCancelButton
        val currentUsername = dialogView.currentUsername

        currentUsername.text = context.getString(
            R.string.currentName,
            FirebaseAuth.getInstance().currentUser?.displayName
        )

        confirmButton.setOnClickListener {
            val username = newName.text.toString()
            val usernameOK = username.length > 5
            if (!usernameOK) {
                newName.error = context.getString(R.string.invalidUsername)
                return@setOnClickListener
            }
            updateUserName(username, context)
            dialogBuilder.dismiss()

        }
        cancelButton.setOnClickListener {
            dialogBuilder.dismiss()
        }

        with(dialogBuilder) {
            setView(dialogView)
            show()
        }
    }

    private fun updateUserName(username: String, context: Context) {
        val auth = FirebaseAuth.getInstance()
        val uid = auth.uid
        auth.currentUser!!.updateProfile(
            UserProfileChangeRequest.Builder().setDisplayName(username).build()
        ).addOnSuccessListener {
            context.showMessage(context.getString(R.string.nameChanged, username))
        }

        FirebaseFirestore.getInstance().collection(
            "users"
        ).document(uid!!).update("name", username)

    }

    private fun registerNow(it: View) {
        val navOptions =
            NavOptions.Builder().setPopUpTo(R.id.settingsFragment, false).build()
        it.findNavController().navigate(
            R.id.registerFragment,
            null, navOptions
        )
    }

    private fun changeFontDialog(textView: View) {
        val context = textView.context
        val sharedPrefs = context.getSharedPreferences("UserPrefs", 0)

        val dialogBuilder = AlertDialog.Builder(context).create()
        val dialogView = with(LayoutInflater.from(context)) {
            inflate(
                R.layout.change_font_dialog,
                null
            )
        }
        val fontCairo = dialogView.fontCairo
        val fontMada = dialogView.fontMada
        val fontTaj = dialogView.fontTajawal

        val fontAlMar = dialogView.fontAlMarai

        val fonts = listOf(fontCairo, fontMada, fontTaj, fontAlMar)
        val currentFont = Font.valueOf(sharedPrefs.getString("fontPref", Font.Cairo.toString())!!)

        fun changeFont(font: Font) {
            sharedPrefs.edit().putString("fontPref", font.toString()).apply()
            recreateActivity()
        }

        dialogView.fontChangeCancelButton.setOnClickListener {
            dialogBuilder.dismiss()
        }

        for (fontButton in fonts)
            fontButton.setOnClickListener {
                when (it) {

                    fontCairo ->
                        changeFont(Font.Cairo)


                    fontMada ->
                        changeFont(Font.Mada)


                    fontTaj ->
                        changeFont(Font.Taj)

                    fontAlMar ->
                        changeFont(Font.AlMar)


                }
                dialogBuilder.dismiss()
            }



        fun tickThis(textView: AppCompatTextView) {
            textView.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_check_circle
                ), null, null, null
            )
            textView.setTypeface(textView.typeface, Typeface.BOLD)
            textView.textSize = 26f
        }



        when (currentFont) {
            Font.Cairo -> tickThis(fontCairo)
            Font.Mada -> tickThis(fontMada)
            Font.Taj -> tickThis(fontTaj)
            Font.AlMar -> tickThis(fontAlMar)
        }

        with(dialogBuilder) {
            setView(dialogView)
            show()
        }

    }


    private fun tintDrawable(textView: AppCompatTextView) {
        // sets circle color to match user current preference
        val context = textView.context
        val sharedPrefs = context.getSharedPreferences("UserPrefs", 0)

        val mainColor = sharedPrefs.getInt("mainColor", StatusColor.Blue.value)
        var drawable = ContextCompat.getDrawable(context, R.drawable.ic_check_circle)
        drawable = DrawableCompat.wrap(drawable!!)
        DrawableCompat.setTint(drawable, ContextCompat.getColor(context, mainColor))
        DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN)
        textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)

    }

    private fun colorDialog(view: View) {
        val context = view.context
        val sharedPrefs = context.getSharedPreferences("UserPrefs", 0)
        val dialogBuilder = AlertDialog.Builder(context).create()
        val dialogView = with(LayoutInflater.from(context)) {
            inflate(
                R.layout.main_color_pref_dialog,
                null
            )
        }
        val colors = listOf(
            StatusColor.Blue,
            StatusColor.Red,
            StatusColor.Green,
            StatusColor.Black
        )
        var statusColor = StatusColor.Blue

        val mainColor = sharedPrefs
            .getInt("mainColor", StatusColor.Blue.value)


        when (mainColor) {
            R.color.colorPrimaryDark -> statusColor = StatusColor.Blue
            R.color.magentaPurple -> statusColor = StatusColor.Red
            R.color.darkGreen -> statusColor = StatusColor.Green
            R.color.superBlack -> statusColor = StatusColor.Black
        }

        with(dialogBuilder) {
            setView(dialogView)
            show()
        }
        val colorsTexts = context.resources.getStringArray(R.array.statusColors)
        val colorAdapter = ArrayAdapter<String>(
            context!!, R.layout.app_spinner, colorsTexts
        )


        val spinner = dialogView.dialog_status_color_spinner
        val imageColor = dialogView.dialog_statusColorImage
        val doneButton = dialogView.statusColor_dialog_done

        spinner.adapter = colorAdapter
        // set current color in spinner and image
        spinner.setSelection(colors.indexOf(statusColor))

        imageColor.setColorFilter(
            ContextCompat.getColor(context, mainColor), PorterDuff.Mode.SRC_IN
        )


        imageColor.setOnClickListener {
            spinner.performClick()

        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                statusColor = colors[position]
                imageColor.setColorFilter(
                    ContextCompat.getColor(context, statusColor.value), PorterDuff.Mode.SRC_IN
                )
                sharedPrefs.edit().putInt("mainColor", statusColor.value).apply()

            }
        }

        doneButton.setOnClickListener {
            dialogBuilder.dismiss()
            view.recreateFragment(R.id.settingsFragment)
        }


    }

    private fun changeTheme(sharedPrefs: SharedPreferences, isDark: Boolean) {

        // put new preference
        sharedPrefs.edit().putBoolean("isDarkTheme", isDark).apply()

        // recreate activity after 100ms
        recreateActivity()

    }

    private fun recreateActivity() {
        Handler().postDelayed({
            TaskStackBuilder.create(activity)
                .addNextIntent(Intent(activity, MainActivity::class.java))
                .addNextIntent(activity.intent)
                .startActivities()
        }, 100)
    }

    private fun faq(view: View) =
        view.findNavController().navigate(R.id.faqFragment)


    private fun clearCache(view: View) =
        with(view.context) {
            cacheDir.deleteRecursively()
            showMessage(getString(R.string.cache_deleted))
        }


    private fun moreApps(view: View) =
        with(view.context) {
            val url = getString(R.string.storeURL)
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }


    private fun showPrivacyPolicy(view: View) {
        val context = view.context
        val privacyPolicyText = context.getString(R.string.privacyPolicyText)
        val privacyPolicyTextAr = context.getString(R.string.privacyPolicyTextAr)

        val dialog = AlertDialog.Builder(context)

        dialog.setPositiveButton(context!!.getString(R.string.ok)) { firstDialog, _ ->
            firstDialog.dismiss()
        }

        dialog.setNegativeButton(context.getString(R.string.changeLanguage)) { firstDialog, _ ->
            val anotherDialog = AlertDialog.Builder(context)
            anotherDialog.setPositiveButton(context.getString(R.string.okay)) { secondDialog, _ ->
                secondDialog.dismiss()
            }
            anotherDialog.setTitle(context.getString(R.string.privacyPolicyTitleAr))
            anotherDialog.setMessage(privacyPolicyTextAr)
            anotherDialog.show()
            firstDialog.dismiss()
        }
        dialog.setTitle(context.getString(R.string.privacyPolicyTitle))
        dialog.setMessage(privacyPolicyText)
        dialog.show()
    }

    private fun signOut(view: View) {
        val uid = FirebaseAuth.getInstance().uid!!
        fun signOutNow() {
            FirebaseFirestore.getInstance().collection("users").document(uid)
                .update("online", false)
            FirebaseMessaging.getInstance().unsubscribeFromTopic(uid)
            FirebaseAuth.getInstance().signOut()
            view.recreateFragment(R.id.settingsFragment)
        }

        val context = view.context
        context.showYesNoDialog(
            { signOutNow() },
            {},
            context.getString(R.string.logout),
            context.getString(R.string.logout_confirm)
        )

    }


}



