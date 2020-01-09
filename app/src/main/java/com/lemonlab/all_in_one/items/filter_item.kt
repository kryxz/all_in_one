package com.lemonlab.all_in_one.items

import com.lemonlab.all_in_one.R
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import ja.burhanrashid52.photoeditor.PhotoFilter
import kotlinx.android.synthetic.main.filter_item.view.*

class FilterItem(
    private var image: Int, private var filterType: PhotoFilter,
    private var action: (PhotoFilter) -> Unit
) : Item<ViewHolder>() {


    override fun getLayout() =
        R.layout.filter_item


    override fun bind(viewHolder: ViewHolder, position: Int) {
        val imageView = viewHolder.itemView.filter_item_image_view
        imageView.setImageResource(image)

        imageView.setOnClickListener {
            action.invoke(filterType)
        }
    }

}