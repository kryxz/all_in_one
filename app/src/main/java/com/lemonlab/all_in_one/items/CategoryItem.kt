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


class CategoryItem(private val category: Category) :

    Item<ViewHolder>() {
    override fun getLayout() =
        R.layout.category_view


    private val pic = CategoryPics.getPics(category)[Random.nextInt(CategoryPics.size)]

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val view = viewHolder.itemView
        val context = view.context
        val categoryText = context.getString(category.textID)
        view.category_tv.text = context.highlightText(categoryText)
        view.category_image.setImageResource(pic)

        view.setOnClickListener {
            it.findNavController()
                .navigate(AllCategoriesFragmentDirections.categoryToQuotes().setCategory(category))
        }

    }

}


enum class MainItem {
    Favorites,
    UsersTexts,
    Pictures,
    Quotes
}


class MainFragmentItem(private val mainItem: MainItem) :

    Item<ViewHolder>() {
    override fun getLayout() =
        R.layout.category_view


    private val pic = CategoryPics.getRandomPic()

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val view = viewHolder.itemView

        view.category_image.setImageResource(pic)
        val context = view.context
        val text = when (mainItem) {
            MainItem.Favorites -> context.getString(R.string.favorites)
            MainItem.UsersTexts -> context.getString(R.string.usersTexts)
            MainItem.Pictures -> context.getString(R.string.pictures)
            MainItem.Quotes -> context.getString(R.string.all_categories)
        }

        view.category_tv.text = context.highlightText(text)

        view.setOnClickListener {
            when (mainItem) {
                MainItem.Favorites -> {
                    it.findNavController()
                        .navigate(MainFragmentDirections.mainToFavorites())

                }
                MainItem.UsersTexts -> {
                    it.findNavController()
                        .navigate(MainFragmentDirections.mainToUsersTexts())
                }
                MainItem.Pictures -> {
                    // TODO: navigate to pictures fragment
                }
                MainItem.Quotes -> {
                    it.findNavController()
                        .navigate(MainFragmentDirections.mainToCategories())
                }
            }

        }


    }

}
