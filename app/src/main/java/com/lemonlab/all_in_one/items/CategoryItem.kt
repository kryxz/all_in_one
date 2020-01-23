package com.lemonlab.all_in_one.items

import androidx.navigation.findNavController
import com.lemonlab.all_in_one.AllCategoriesFragmentDirections
import com.lemonlab.all_in_one.MainFragmentDirections
import com.lemonlab.all_in_one.R
import com.lemonlab.all_in_one.extensions.highlightText
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.category_view.view.*
import kotlin.random.Random


class CategoryItem(private val category: Category, private val showImage: Boolean) :

    Item<ViewHolder>() {
    override fun getLayout() =
        if (showImage) R.layout.category_view else
            R.layout.category_text_item


    private val pic = CategoryPics.getPics(category)[Random.nextInt(CategoryPics.size)]

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val view = viewHolder.itemView
        val context = view.context


        val categoryText = context.getString(category.textID)

        if (showImage) {
            view.category_tv.text = context.highlightText(categoryText)
            view.category_image.setImageResource(pic)
        } else
            view.category_tv.text = categoryText

        view.setOnClickListener {
            it.findNavController()
                .navigate(AllCategoriesFragmentDirections.categoryToQuotes().setCategory(category))
        }

    }

}


enum class MainItem(val textID: Int) {
    Favorites(R.string.favorites),
    UsersTexts(R.string.usersTexts),
    Pictures(R.string.pictures),
    Quotes(R.string.all_categories),
    StatusCreate(R.string.statusCreate)
}


class MainFragmentItem(private val mainItem: MainItem) :

    Item<ViewHolder>() {
    override fun getLayout() =
        R.layout.category_view

    private val pic = CategoryPics.getRandomPic()

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val view = viewHolder.itemView

        val context = view.context
        val text = context.getString(mainItem.textID)
        view.category_image.setImageResource(pic)
        view.category_tv.text = context.highlightText(text)


        view.setOnClickListener {
            val navController = it.findNavController()
            when (mainItem) {
                MainItem.Favorites -> {
                    navController
                        .navigate(MainFragmentDirections.mainToFavorites())

                }
                MainItem.UsersTexts -> {
                    navController
                        .navigate(MainFragmentDirections.mainToUsersTexts())
                }
                MainItem.Pictures -> {
                    navController.navigate(R.id.picturesFragment)
                }
                MainItem.Quotes -> {
                    navController
                        .navigate(MainFragmentDirections.mainToCategories())
                }
                MainItem.StatusCreate -> {
                    navController
                        .navigate(
                            MainFragmentDirections
                                .mainToDecorate("", Category.Other)
                        )
                }

            }

        }


    }

}
