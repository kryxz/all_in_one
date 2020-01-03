package com.lemonlab.all_in_one.items

import android.app.AlertDialog
import android.graphics.Typeface
import com.lemonlab.all_in_one.R
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.font_item_view.view.*

class FontItem(private var fontFace: Typeface, private var action:(Typeface)-> Unit,
               private var dialog: AlertDialog):Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.font_item_view
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val textView = viewHolder.itemView.font_item_text_view
        textView.typeface = fontFace // change type
        textView.text = "بسم الله الرحمن الرحيم" //TODO:: change to sample text

        // pass the text to action and invoke it, this will trigger the action and change the editor
        // current font

        textView.setOnClickListener {
            action.invoke(fontFace)
            dialog.dismiss()
        }
    }
}