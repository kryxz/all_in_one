package com.lemonlab.all_in_one.items

import androidx.appcompat.widget.AppCompatImageView
import com.lemonlab.all_in_one.FavoritesFragment
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
        view.fav_text_tv.text = context.highlightText(text)
        view.fav_text_image.setImageResource(pic)

        view.fav_download_btn.setOnClickListener {
            val bitmap = view.fav_item_layout.getBitmapFromView()
            QuoteItem.saveImage(bitmap, context)
        }

        view.fav_content_btn.setOnClickListener {
            QuoteItem.copyItem(context = context, text = text, button = it as AppCompatImageView)
        }

        view.fav_delete_btn.setOnClickListener {
            deleteFav()
        }

        view.fav_whats_share_btn.setOnClickListener {
            QuoteItem.shareWhatsApp(text, context)
        }

        view.fav_share_btn.setOnClickListener {
            QuoteItem.shareText(text, context)

        }

    }

    private fun deleteFav() =
        FavoritesFragment.favoritesViewModel.remove(favorite)

}

