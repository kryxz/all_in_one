package com.lemonlab.all_in_one.items

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.lemonlab.all_in_one.R
import com.lemonlab.all_in_one.extensions.showMessage
import com.lemonlab.all_in_one.extensions.showYesNoDialog
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.settings_item_text.view.*

enum class Option {
    ClearCache,
    SignOut,
    DarkTheme,
    MoreApps,
    PrivacyPolicy,
    FAQ,

}


class SettingsItem(
    private val text: String,
    private val option: Option
) :
    Item<ViewHolder>() {

    override fun getLayout() = R.layout.settings_item_text


    override fun bind(viewHolder: ViewHolder, position: Int) {
        val view = viewHolder.itemView
        val textView = view.settingsTextView
        val switch = view.settingsSwitch

        fun setIcon(icon: Int) {
            val drawable = ContextCompat.getDrawable(view.context, icon)!!
            textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
        }


        textView.text = text

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
                switch.visibility = View.VISIBLE
                switch.setOnCheckedChangeListener { button, isChecked ->
                    changeTheme(button)
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


    private fun changeTheme(view: View) {

    }

    private fun faq(view: View) {

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