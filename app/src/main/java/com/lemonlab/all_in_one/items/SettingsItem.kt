package com.lemonlab.all_in_one.items

import android.app.Activity
import android.app.AlertDialog
import android.app.TaskStackBuilder
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.view.View
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.lemonlab.all_in_one.MainActivity
import com.lemonlab.all_in_one.R
import com.lemonlab.all_in_one.extensions.showMessage
import com.lemonlab.all_in_one.extensions.showYesNoDialog
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.settings_item_text.view.*

enum class Option(val textID: Int) {
    ClearCache(R.string.cache_delete),
    SignOut(R.string.signOut),
    DarkTheme(R.string.darkMode),
    MoreApps(R.string.moreApps),
    PrivacyPolicy(R.string.privacyPolicyTitleAr),
    FAQ(R.string.faq),

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
        fun setIcon(icon: Int) {
            val drawable = ContextCompat.getDrawable(context, icon)!!
            textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
        }


        textView.text = context.getString(option.textID)

        when (option) {
            Option.ClearCache -> {
                setIcon(R.drawable.ic_clear_all)
                textView.setOnClickListener { clearCache(it) }
            }

            Option.SignOut -> {
                setIcon(R.drawable.ic_logout)
                textView.setOnClickListener { signOut(it) }
            }
            Option.DarkTheme -> {
                val sharedPrefs = context.getSharedPreferences("UserPrefs", 0)
                switch.isChecked = sharedPrefs.getBoolean("isDarkTheme", false)
                switch.visibility = View.VISIBLE
                switch.setOnCheckedChangeListener { button, isChecked ->
                    changeTheme(button, isChecked)
                }

            }
            Option.MoreApps -> {
                setIcon(R.drawable.ic_apps)
                textView.setOnClickListener { moreApps(it) }
            }
            Option.PrivacyPolicy -> {
                setIcon(R.drawable.ic_info)
                textView.setOnClickListener { showPrivacyPolicy(it) }
            }
            Option.FAQ -> {
                setIcon(R.drawable.ic_question_answer)
                textView.setOnClickListener { faq(it) }
            }
        }

    }


    private fun changeTheme(view: CompoundButton, isDark: Boolean) {

        val context = view.context

        val sharedPrefs = context.getSharedPreferences("UserPrefs", 0)

        // put new preference
        sharedPrefs.edit().putBoolean("isDarkTheme", isDark).apply()

        // restart activity
        Handler().postDelayed({
            TaskStackBuilder.create(activity)
                .addNextIntent(Intent(activity, MainActivity::class.java))
                .addNextIntent(activity.intent)
                .startActivities()
        }, 100)
    }

    private fun faq(view: View) {
        view.findNavController().navigate(R.id.faqFragment)
    }

    private fun clearCache(view: View) {

        with(view.context) {
            cacheDir.deleteRecursively()
            showMessage(getString(R.string.cache_deleted))
        }

    }

    private fun moreApps(view: View) {
        with(view.context) {
            val url = getString(R.string.storeURL)
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }
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


}


private fun signOut(view: View) {
    fun signOutNow() = FirebaseAuth.getInstance().signOut()
    val context = view.context
    context.showYesNoDialog(
        { signOutNow() },
        {},
        context.getString(R.string.logout),
        context.getString(R.string.logout_confirm)
    )

}


