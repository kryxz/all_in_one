package com.lemonlab.all_in_one


import android.app.AlertDialog
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.gms.ads.InterstitialAd
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lemonlab.all_in_one.extensions.*
import com.lemonlab.all_in_one.items.Category
import com.lemonlab.all_in_one.items.categories
import com.lemonlab.all_in_one.model.StatusColor
import com.lemonlab.all_in_one.model.UserStatus
import kotlinx.android.synthetic.main.fragment_send.*
import java.sql.Timestamp
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

/*
    Users can send texts.
 */

class SendFragment : Fragment() {

    private val fullScreenAd: InterstitialAd by lazy {
        InterstitialAd(context!!)
    }

    private var statusColor = StatusColor.Blue
    private var statusCategory = Category.Other

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_send, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.checkUser()
        init()
        Ads.loadFullScreenAd(fullScreenAd)

    }

    private fun init() {
        // set spinner items
        val category = resources.getStringArray(R.array.category)


        // categories adapter
        val dataAdapter =
            ArrayAdapter<String>(context!!, R.layout.app_spinner, category)

        send_status_category.adapter = dataAdapter

        // handle changes on categories spinner
        send_status_category.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                statusCategory = categories[position]
            }
        }
        send_status_category.setSelection(categories.indexOf(statusCategory))
        // status color adapter
        val colorsTexts = resources.getStringArray(R.array.statusColors)
        val colorAdapter = ArrayAdapter<String>(
            context!!, R.layout.app_spinner, colorsTexts
        )
        status_color_spinner.adapter = colorAdapter

        send_status_send_btn.setOnClickListener {
            sendTextUserStatus()
        }

        // handle changes on colors spinner
        colorStatusSpinner()

        // show preview status dialog on button click
        previewStatusButton()

        // navigate user to send image fragment

        send_status_image_btn.setOnClickListener {
            val direction =
                SendFragmentDirections.sendImage(null)
            view!!.findNavController().navigate(direction)
        }
    }

    private fun colorStatusSpinner() {


        val colors = listOf(
            StatusColor.Blue,
            StatusColor.Red,
            StatusColor.Green,
            StatusColor.Black
        )

        fun setUIColor(index: Int) {
            statusColorImage.setColorFilter(
                ContextCompat.getColor(
                    context!!,
                    colors[index].value
                ), PorterDuff.Mode.SRC_IN
            )
        }

        statusColorImage.setOnClickListener {
            status_color_spinner.performClick()
        }
        status_color_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                setUIColor(position)
                statusColor = colors[position]
            }
        }


    }

    // show preview status dialog on button click
    private fun previewStatusButton() {

        // declare builder and view

        val dialogBuilder = AlertDialog.Builder(context!!).create()
        val dialogView = layoutInflater.inflate(
            R.layout.user_status_preview,
            view!!.findViewById(R.id.sendFragment)
        )


        // declare and define views
        val statusTextView =
            dialogView.findViewById<AppCompatTextView>(R.id.preview_user_status_text)
        val usernameTextView =
            dialogView.findViewById<AppCompatTextView>(R.id.preview_status_username)
        val dateTextView = dialogView.findViewById<AppCompatTextView>(R.id.preview_status_date)
        val likesTextView =
            dialogView.findViewById<AppCompatTextView>(R.id.preview_status_likes)
        val okayButton = dialogView.findViewById<AppCompatButton>(R.id.preview_status_done_btn)


        send_status_preview_status.setOnClickListener {
            val text = send_status_edit_text.text.toString().removeWhitespace()
            if (text.isEmpty()) return@setOnClickListener


            tintDrawable(likesTextView)
            dialogBuilder.setView(dialogView)
            dialogBuilder.show()

            okayButton.setOnClickListener {
                dialogBuilder.dismiss()
            }
            // show a formatted date.
            dateTextView.text =
                getDateAsString(Timestamp(System.currentTimeMillis()))

            statusTextView.text = context!!.highlightTextWithColor(statusColor.value, text)
            usernameTextView.text = FirebaseAuth.getInstance().currentUser?.displayName
            likesTextView.text = Random.nextInt(100).toString()

        }


    }

    private fun tintDrawable(editText: AppCompatTextView) {
        var drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_favorite)
        drawable = DrawableCompat.wrap(drawable!!)
        DrawableCompat.setTint(
            drawable,
            ContextCompat.getColor(context!!, statusColor.value)
        )
        DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN)
        editText.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)

    }

    private fun sendTextUserStatus() {
        val text = send_status_edit_text.text.toString().removeWhitespace()

        if (text.isEmpty()) {
            context!!.showMessage(getString(R.string.warningTextEmpty))
            return
        }
        fullScreenAd.show()
        val id = FirebaseAuth.getInstance().uid

        sendFragmentView.visibility = View.GONE
        textSendingProgressBar.visibility = View.VISIBLE
        val userName = FirebaseAuth.getInstance().currentUser!!.displayName
        val statusID =
            "$userName-" + UUID.randomUUID().toString().substring(0, 16).replace("-", "")

        val userStatus = UserStatus(
            text = text,
            category = statusCategory,
            statusColor = statusColor,
            timestamp = Timestamp(System.currentTimeMillis()),
            userID = id.toString(),
            statusID = statusID,
            reportsIDs = ArrayList(),
            likesIDs = ArrayList()
        )
        activity!!.hideKeypad()
        val db = FirebaseFirestore.getInstance()
        db.collection("statuses").document(statusID).set(userStatus).addOnSuccessListener {
            sendFragmentView.visibility = View.VISIBLE
            textSendingProgressBar.visibility = View.GONE
            context!!.showMessage(getString(R.string.statusSent))
            send_status_edit_text.text!!.clear()
            view!!.findNavController().navigate(R.id.usersTextsFragment)

        }.addOnFailureListener {
            context!!.showMessage(getString(R.string.warningSentStatus))
        }

    }

}
