package com.lemonlab.all_in_one.items

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.navigation.findNavController
import com.lemonlab.all_in_one.FavoritesFragment
import com.lemonlab.all_in_one.FavoritesFragmentDirections
import com.lemonlab.all_in_one.R
import com.lemonlab.all_in_one.extensions.getBitmapFromView
import com.lemonlab.all_in_one.extensions.highlightText
import com.lemonlab.all_in_one.items.CategoryPics.Companion.getIndexPic
import com.lemonlab.all_in_one.model.Favorite
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.favorite_item.view.*

class FavoriteItem(
    private val favorite: Favorite,
    indices: Pair<Int, Int>
) :
    Item<ViewHolder>() {
    override fun getLayout() =
        R.layout.favorite_item


    private val pic = getIndexPic(indices.first, indices.second)
    private val text = favorite.text

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val view = viewHolder.itemView
        val context = view.context

        with(view) {
            fav_text_tv.text = context.highlightText(text)
            fav_text_image.setImageResource(pic)

            val allButtons = listOf(
                fav_download_btn, fav_content_btn,
                fav_delete_btn, fav_whats_share_btn,
                fav_share_btn, fav_decorate_btn
            )

            for (button in allButtons) {
                button.setOnClickListener {

                    when (button) {

                        fav_download_btn ->
                            QuoteItem.saveImage(view.fav_item_layout.getBitmapFromView(), context)

                        fav_content_btn ->
                            QuoteItem.copyItem(
                                context = context, text = text,
                                button = it as AppCompatImageView
                            )

                        fav_delete_btn -> deleteFav()

                        fav_whats_share_btn -> QuoteItem.shareWhatsApp(text, context)

                        fav_share_btn -> QuoteItem.shareText(text, context)


                        fav_decorate_btn -> decorateText(it)
                    }
                }
            }
        }

    }

    private fun decorateText(it: View) {
        val direction =
            FavoritesFragmentDirections.decorateTextNow(text, favorite.category)
        it.findNavController().navigate(direction)
    }


    private fun deleteFav() =
        FavoritesFragment.favoritesViewModel.remove(favorite)

}

