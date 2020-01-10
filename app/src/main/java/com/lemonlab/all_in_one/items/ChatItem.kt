package com.lemonlab.all_in_one.items

import android.content.Context
import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lemonlab.all_in_one.R
import com.lemonlab.all_in_one.extensions.highlightText
import com.lemonlab.all_in_one.model.Message
import com.lemonlab.all_in_one.model.User
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.chat_item.view.*

class ChatItem(private val message: Message) : Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.chat_item
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val view = viewHolder.itemView
        // set the message text and the username
        val context = view.context
        view.chat_tv.text = context.highlightText(message.text)
        view.chat_item_username_tv.text = message.username

        // mark this message with new color
        val uid = FirebaseAuth.getInstance().uid
        if (message.userUid == uid.toString()) {
            view.chat_item_username_tv.setTextColor(ContextCompat.getColor(context, R.color.green))
        }

        checkUserStatus(context, viewHolder)
    }

    private fun checkUserStatus(context: Context, viewHolder: ViewHolder) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(message.userUid).get().addOnSuccessListener {
            if (it != null && it.exists()) {
                val user = it.toObject(User::class.java)
                if (user!!.online == "true") {
                    ImageViewCompat.setImageTintList(
                        viewHolder.itemView.chat_item_user_status_image_view
                        , ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green))
                    )
                } else {
                    ImageViewCompat.setImageTintList(
                        viewHolder.itemView.chat_item_user_status_image_view
                        , ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red))
                    )
                }
            }
        }
    }
}