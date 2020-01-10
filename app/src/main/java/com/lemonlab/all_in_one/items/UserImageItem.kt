package com.lemonlab.all_in_one.items

import com.lemonlab.all_in_one.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.user_image_item.view.*

class UserImageItem(private var imageUrl:String):Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.user_image_item
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        Picasso.get().load(imageUrl).into(
            viewHolder.itemView.user_image_image_view
        )
    }
}