package com.lemonlab.all_in_one.items

import android.content.*
import android.os.Handler
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.lemonlab.all_in_one.R
import com.lemonlab.all_in_one.extensions.highlightText
import com.lemonlab.all_in_one.extensions.showMessage
import com.lemonlab.all_in_one.items.CategoryPics.Companion.getPics
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.quote_item.view.*
import kotlin.random.Random


enum class Category { Wisdom, Friendship, Sadness, Islam, Other, Morning, Afternoon, Love, Winter }

// used to get the category of the quote using categories.indexOf(category)
val categories = listOf(
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
        const val size = 6

        private val wisdom = listOf(
            R.drawable.coffee_book,
            R.drawable.hourglass,
            R.drawable.book_dark,
            R.drawable.three_books,
            R.drawable.book,
            R.drawable.notepad_ideas
        )

        private val friendship = listOf(
            R.drawable.friend_girls,
            R.drawable.friend_guys,
            R.drawable.friends_kids,
            R.drawable.friends_sea,
            R.drawable.friends_smile,
            R.drawable.friends_star
        )


        private val sadness = listOf(
            R.drawable.guy_sad,
            R.drawable.girl_sad,
            R.drawable.sadness_cap,
            R.drawable.sadness_girl,
            R.drawable.sadness_sea,
            R.drawable.sadness_think
        )


        private val islam = listOf(
            R.drawable.man_islam,
            R.drawable.mosque_islam,
            R.drawable.islam_camels,
            R.drawable.islam_madina,
            R.drawable.islam_sunset,
            R.drawable.islam_masjid
        )

        private val other = listOf(
            R.drawable.hand_other,
            R.drawable.flower_other,
            R.drawable.artist_other,
            R.drawable.museum_other,
            R.drawable.painting_other,
            R.drawable.women_other
        )

        private val morning = listOf(
            R.drawable.city_morning,
            R.drawable.field_morning,
            R.drawable.morning_coffee,
            R.drawable.morning_grass,
            R.drawable.morning_woman,
            R.drawable.morning_latte
        )


        private val afternoon = listOf(
            R.drawable.man_afternoon,
            R.drawable.sign_afternoon,
            R.drawable.afternoon_lantern,
            R.drawable.afternoon_light,
            R.drawable.afternoon_man,
            R.drawable.afternoon_umbrella
        )


        private val love = listOf(
            R.drawable.bicycle_love,
            R.drawable.couple_love,
            R.drawable.love_abstract,
            R.drawable.love_birds,
            R.drawable.love_hands,
            R.drawable.love_sun
        )


        private val winter = listOf(
            R.drawable.winter_girl,
            R.drawable.winter_snow,
            R.drawable.winter_sunset,
            R.drawable.winter_bench,
            R.drawable.winter_coffee,
            R.drawable.winter_man
        )

        val allPics =
            listOf(wisdom, friendship, sadness, islam, other, morning, afternoon, love, winter)

        // returns a list of pictures ids for a category
        fun getPics(category: Category) =
            allPics[categories.indexOf(category)]
    }

}


class QuoteItem(
    private val context: Context,
    private val text: String,
    category: Category
) :
    Item<ViewHolder>() {
    override fun getLayout() =
        R.layout.quote_item

    private val pic = getPics(category)[Random.nextInt(CategoryPics.size)]

    override fun bind(viewHolder: ViewHolder, position: Int) {
        // used instead of viewHolder.itemView.etc
        val view = viewHolder.itemView

        // set text and background picture.
        view.quote_text_tv.text = context.highlightText(text)
        view.text_image.setImageResource(pic)

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
                        copyItem(context, text, button as AppCompatImageView)


                }
            }
    }



    // copies item to clipboard and shows a message!
    private fun copyItem(context: Context, text: String, button: AppCompatImageView) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("text", text)
        clipboard.setPrimaryClip(clip)
        context.showMessage(context.getString(R.string.copied))
        with(button) {
            setImageResource(R.drawable.ic_done)
            Handler().postDelayed({ setImageResource(R.drawable.ic_copy) }, 500)
        }
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
            context.showMessage(context.getString(R.string.appNotFound))
        }
    }


    private fun favorite() {
        // TODO:: Implement a database to store favorites.

    }


}