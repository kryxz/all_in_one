package com.lemonlab.all_in_one.items

import android.app.AlertDialog
import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import com.lemonlab.all_in_one.R
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.sticker_item_view.view.*

class StickerItem(var context: Context, private var emojiCode:String, private var action: (TextView)-> Unit,
                  private var dialog:AlertDialog,
                  private var spanCount:Int): Item<ViewHolder>(){

    override fun getLayout(): Int {
        return R.layout.sticker_item_view
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        // change text size using span count
        val size = 100
        viewHolder.itemView.sticker_image_view.textSize = size - (((spanCount - 1)/10f) * size)

        viewHolder.itemView.sticker_image_view.text = emojiCode

        // select this image, and trigger the action
        // the action will pass this image view to the photo editor
        viewHolder.itemView.sticker_image_view.setOnClickListener {
            action.invoke(viewHolder.itemView.sticker_image_view)
            dialog.dismiss()
        }
    }

}
