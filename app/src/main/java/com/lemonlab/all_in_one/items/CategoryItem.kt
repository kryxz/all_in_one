package com.lemonlab.all_in_one.items

import android.content.Context
import android.widget.Toast
import com.lemonlab.all_in_one.R
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.category_view.view.*


class CategoryItem(
    private val context: Context,
    private val imageID: Int,
    private val categoryText: String
) :
    Item<ViewHolder>() {
    override fun getLayout() =
        R.layout.category_view

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val view = viewHolder.itemView
        view.category_tv.text = categoryText
        view.category_image.setImageResource(imageID)
        view.setOnClickListener {
            Toast.makeText(context, "Clicked", Toast.LENGTH_LONG).show()
        }
    }

}