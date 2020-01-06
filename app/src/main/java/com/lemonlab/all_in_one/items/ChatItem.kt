package com.lemonlab.all_in_one.items

import android.content.Context
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.lemonlab.all_in_one.R
import com.lemonlab.all_in_one.extensions.highlightText
import com.lemonlab.all_in_one.model.Message
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.chat_item.view.*
import kotlinx.android.synthetic.main.fragment_must_login.view.*

class ChatItem(var message: Message, var context: Context) : Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.chat_item
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val view = viewHolder.itemView
        // set the message text and the username

        view.chat_tv.text = context.highlightText(message.text)
        view.chat_item_username_tv.text = message.username

        // mark this message with new color
        val uid = FirebaseAuth.getInstance().uid
        if (message.userUid == uid.toString()) {
            view.chat_item_username_tv.setTextColor(ContextCompat.getColor(context, R.color.green))
        }

        // set profile for user if does not have one
        if(message.username.length >= 2){
            view.chat_item_user_image_title.text = message.username.substring(0, 2)
        }else{
            view.chat_item_user_image_title.text = message.username[0].toString()
        }
    }
}