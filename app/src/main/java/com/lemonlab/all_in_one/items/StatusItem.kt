package com.lemonlab.all_in_one.items

import com.lemonlab.all_in_one.R
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

class StatusItem() : Item<ViewHolder>() {

    override fun getLayout(): Int {
        return R.layout.status_item_view;
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        // call when create this item
    }

}