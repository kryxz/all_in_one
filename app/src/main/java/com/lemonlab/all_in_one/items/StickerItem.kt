package com.lemonlab.all_in_one.items

import android.content.Context
import com.lemonlab.all_in_one.R
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.sticker_item_view.view.*

class StickerItem(var context: Context): Item<ViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.sticker_item_view
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.sticker_image_view.background = context.getDrawable(R.drawable.girl)
    }

}
