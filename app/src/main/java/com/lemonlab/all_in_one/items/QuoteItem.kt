package com.lemonlab.all_in_one.items

import android.content.*
import android.graphics.Color
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.view.View
import com.lemonlab.all_in_one.R
import com.lemonlab.all_in_one.extensions.showMessage
import com.lemonlab.all_in_one.items.CategoryPics.Companion.allPics
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.quote_item.view.*
import kotlin.random.Random


enum class Category { Wisdom, Friendship, Sadness, Islam, Other, Morning, Afternoon, Love, Winter }

// used to get the category of the quote using categories.indexOf(category)
private val categories = listOf(
    Category.Wisdom,
    Category.Friendship,
    Category.Sadness,
    Category.Islam,
    Category.Other,
    Category.Morning,
    Category.Afternoon,
    Category.Love,
    Category.Winter
)

// pictures for each category. Will add more later.
class CategoryPics {
    companion object {
        private val wisdom = listOf(R.drawable.coffee_book, R.drawable.hourglass)
        private val friendship = listOf(R.drawable.friend_girls, R.drawable.friend_guys)
        private val sadness = listOf(R.drawable.guy_sad, R.drawable.girl_sad)
        private val islam = listOf(R.drawable.man_islam, R.drawable.mosque_islam)
        private val other = listOf(R.drawable.hand_other, R.drawable.flower_other)
        private val morning = listOf(R.drawable.city_morning, R.drawable.field_morning)
        private val afternoon = listOf(R.drawable.man_afternoon, R.drawable.sign_afternoon)
        private val love = listOf(R.drawable.bicycle_love, R.drawable.couple_love)
        private val winter = listOf(R.drawable.winter_girl, R.drawable.winter_snow)

        val allPics =
            listOf(wisdom, friendship, sadness, islam, other, morning, afternoon, love, winter)
    }
}


class QuoteItem(
    private val context: Context,
    private val text: String,
    private val category: Category
) :
    Item<ViewHolder>() {
    override fun getLayout() =
        R.layout.quote_item


    override fun bind(viewHolder: ViewHolder, position: Int) {
        // used instead of viewHolder.itemView.etc
        val view = viewHolder.itemView

        // set text and background picture.
        view.quote_text_tv.text = highlightText(text)
        view.text_image.setImageResource(getPics(category)[Random.nextInt(2)])

        // listens to button clicks and calls a specific function!
        listenButtons(
            listOf<View>(
                view.quote_share_btn,
                view.quote_whats_share_btn,
                view.quote_favorite_btn,
                view.quote_content_btn
            ), view.quote_text_tv.text.toString()
        )

    }

    private fun listenButtons(views: List<View>, text: String) {
        // does the appropriate action depending on which button was clicked!
        for (button in views)
            button.setOnClickListener {
                when (button.id) {

                    R.id.quote_share_btn ->
                        shareText(text)

                    R.id.quote_whats_share_btn ->
                        shareWhatsApp(text)

                    R.id.quote_favorite_btn ->
                        favorite()

                    R.id.quote_content_btn ->
                        copyItem(context, text)

                }
            }
    }


    // returns a list of ids for appropriate category.
    private fun getPics(category: Category) =
        allPics[categories.indexOf(category)]

    // copies item to clipboard and shows a message!
    private fun copyItem(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("text", text)
        clipboard.setPrimaryClip(clip)
        context.showMessage("Copied")
    }

    // shares quote test
    private fun shareText(text: String) {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, text)
        sendIntent.type = "text/plain"
        context.startActivity(sendIntent)
    }

    // if whatsApp exists, share text to it.
    private fun shareWhatsApp(text: String) {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, text)
        sendIntent.type = "text/plain"
        sendIntent.setPackage("com.whatsapp")
        try {
            context.startActivity(sendIntent)
        } catch (e: ActivityNotFoundException) {
            context.showMessage("App Not Found!")
        }
    }


    private fun favorite() {
        // TODO:: Implement a database to store favorites.

    }

    // Highlights text background.
    private fun highlightText(text: String): SpannableString {
        val str = SpannableString(text)
        str.setSpan(BackgroundColorSpan(Color.YELLOW), 0, text.length, 0)
        return str
    }

}