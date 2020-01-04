package com.lemonlab.all_in_one.items

import android.content.Context
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.navigation.findNavController
import com.lemonlab.all_in_one.R
import com.lemonlab.all_in_one.extensions.getBitmapFromView
import com.lemonlab.all_in_one.extensions.highlightText
import com.lemonlab.all_in_one.extensions.showMessage
import com.lemonlab.all_in_one.extensions.showYesNoDialog
import com.lemonlab.all_in_one.items.CategoryPics.Companion.getRandomPic
import com.lemonlab.all_in_one.model.Favorite
import com.lemonlab.all_in_one.model.FavoritesRoomDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.favorite_item.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.*

class FavoriteItem(
    private val context: Context,
    private val favorite: Favorite,
    private val adapter: GroupAdapter<ViewHolder>
) :
    Item<ViewHolder>() {
    override fun getLayout() =
        R.layout.favorite_item


    private val pic = getRandomPic()
    private val text = favorite.text

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val view = viewHolder.itemView

        view.fav_text_tv.text = context.highlightText(text)
        view.fav_text_image.setImageResource(pic)

        view.setOnLongClickListener {
            context.showYesNoDialog({
                val bitMap = view.fav_item_layout.getBitmapFromView()
                // TODO change this to something better!

                MediaStore.Images.Media.insertImage(
                    context.contentResolver,
                    bitMap,
                    "Hello!" + UUID.randomUUID().toString().subSequence(0, 10),
                    "description"
                )


            }, {}, context.getString(R.string.save_image_item), context.getString(
                R.string.save_image_text
            )
            )


            true
        }
        view.fav_content_btn.setOnClickListener {
            QuoteItem.copyItem(context = context, text = text, button = it as AppCompatImageView)
        }
        view.fav_delete_btn.setOnClickListener {
            deleteFav(it)
        }
        view.fav_whats_share_btn.setOnClickListener {
            QuoteItem.shareWhatsApp(text, context)
        }
        view.fav_share_btn.setOnClickListener {
            QuoteItem.shareText(text, context)

        }
    }

    private fun deleteFav(view: View) {

        CoroutineScope(context = Dispatchers.Default).launch {
            val favoriteDao = FavoritesRoomDatabase.getDatabase(context).FavoriteDao()
            favoriteDao.deleteFavorite(favorite)
            Favorites().getFavorites(context)
            this.coroutineContext.cancel()
        }
        adapter.remove(this@FavoriteItem)
        adapter.notifyDataSetChanged()
        if (adapter.itemCount == 0) {
            view.findNavController().navigateUp()
            context.showMessage(context.getString(R.string.no_favorites))

        }
    }


}