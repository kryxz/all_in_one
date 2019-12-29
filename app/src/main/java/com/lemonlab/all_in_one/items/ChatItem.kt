package com.lemonlab.all_in_one.items

import android.content.Context
import android.os.Build
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.lemonlab.all_in_one.R
import com.lemonlab.all_in_one.model.Message
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.chat_item.view.*

class ChatItem(var message: Message, var context: Context): Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.chat_item
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.chat_tv.text = message.text
        viewHolder.itemView.chat_item_username_tv.text = message.username
        val uid = FirebaseAuth.getInstance().uid
        Log.i("chatItem", "uid: $uid")
        Log.i("chatItem", "message.userUid: ${message.userUid}")
        if(message.userUid.equals(uid.toString())){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                viewHolder.itemView.chat_item_username_tv.setTextColor(context.getColor(R.color.green))
            }
        }
    }
}