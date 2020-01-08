package com.lemonlab.all_in_one.items

import android.content.Context
import androidx.core.content.res.ResourcesCompat
import com.lemonlab.all_in_one.R
import com.xwray.groupie.ViewHolder
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.PhotoFilter
import kotlinx.android.synthetic.main.filter_item.view.*

class FilterItem(private var image: Int, private var filterType: PhotoFilter,
                 private var action: (PhotoFilter)-> Unit): com.xwray.groupie.Item<ViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.filter_item
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.itemView.filter_item_image_view.setImageResource(image)

        viewHolder.itemView.filter_item_image_view.setOnClickListener {
            action.invoke(filterType)
        }
    }

}