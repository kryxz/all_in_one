package com.lemonlab.all_in_one.items

import android.content.Context
import androidx.navigation.findNavController
import com.lemonlab.all_in_one.MainFragmentDirections
import com.lemonlab.all_in_one.R
import com.lemonlab.all_in_one.extensions.highlightText
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.category_view.view.*
import kotlin.random.Random


class CategoryItem(
    private val context: Context,
    private val categoryText: String,
    private val category: Category
) :
    Item<ViewHolder>() {
    override fun getLayout() =
        R.layout.category_view


    private val pic = CategoryPics.getPics(category)[Random.nextInt(CategoryPics.size)]

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val view = viewHolder.itemView
        view.category_tv.text = context.highlightText(categoryText)
        view.category_image.setImageResource(pic)

        view.setOnClickListener {
            it.findNavController()
                .navigate(MainFragmentDirections.mainToQuotes().setCategory(category))
        }

    }

}