package com.lemonlab.all_in_one.items

import com.lemonlab.all_in_one.R
import com.lemonlab.all_in_one.model.Message
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.chat_item.view.*

class ChatItem(var message: Message): Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.chat_item
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.chat_edit_text.text = message.text
    }
}