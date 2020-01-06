package com.lemonlab.all_in_one.items

import android.content.Context
import com.lemonlab.all_in_one.R
import com.xwray.groupie.ViewHolder
import ja.burhanrashid52.photoeditor.PhotoFilter

class FilterItem(var filterType: PhotoFilter, var context: Context): com.xwray.groupie.Item<ViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.filter_item
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        // TODO:: Apply filter on the layout's image
    }

}