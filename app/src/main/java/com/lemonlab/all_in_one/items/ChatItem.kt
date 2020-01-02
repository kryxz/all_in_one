package com.lemonlab.all_in_one.items

import android.content.Context
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.lemonlab.all_in_one.R
import com.lemonlab.all_in_one.model.Message
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.chat_item.view.*

class ChatItem(var message: Message, var context: Context) : Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.chat_item
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val view = viewHolder.itemView
        view.chat_tv.text = message.text
        view.chat_item_username_tv.text = message.username
        val uid = FirebaseAuth.getInstance().uid

        if (message.userUid == uid.toString()) {
            view.chat_item_username_tv.setTextColor(ContextCompat.getColor(context, R.color.green))

        }
    }
}