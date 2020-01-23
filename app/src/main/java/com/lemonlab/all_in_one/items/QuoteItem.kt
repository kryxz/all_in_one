package com.lemonlab.all_in_one.items

import android.content.*
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.os.Handler
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.lemonlab.all_in_one.LocalQuotesFragment
import com.lemonlab.all_in_one.LocalQuotesFragmentDirections
import com.lemonlab.all_in_one.R
import com.lemonlab.all_in_one.extensions.getBitmapFromView
import com.lemonlab.all_in_one.extensions.highlightText
import com.lemonlab.all_in_one.extensions.showMessage
import com.lemonlab.all_in_one.items.CategoryPics.Companion.getPics
import com.lemonlab.all_in_one.model.Favorite
import com.lemonlab.all_in_one.model.StatusColor
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.quote_item.view.*
import java.util.*
import kotlin.random.Random


enum class Category(val textID: Int) {
    Wisdom(R.string.wisdom),
    Friendship(R.string.friendship),
    Sadness(R.string.sadness),
    Islam(R.string.islam),
    Other(R.string.other),
    Morning(R.string.morning),
    Afternoon(R.string.afternoon),
    Love(R.string.love),
    Winter(R.string.winter),
    Proverbs(R.string.proverbs),
    Funny(R.string.funny),
    Eid(R.string.eid),
    Ramadan(R.string.ramadan),
    Prayer(R.string.prayer),
}

// used to get the category of the quote using categories.indexOf(category)
// should add any new to this list
val categories = listOf(
    Category.Wisdom,
    Category.Friendship,
    Category.Sadness,
    Category.Islam,
    Category.Other,
    Category.Morning,
    Category.Afternoon,
    Category.Love,
    Category.Funny,
    Category.Winter,
    Category.Proverbs,
    Category.Eid,
    Category.Ramadan,
    Category.Prayer
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

        private val proverbs = listOf(
            R.drawable.coffee_book,
            R.drawable.proverbs_1,
            R.drawable.book_dark,
            R.drawable.three_books,
            R.drawable.proverbs_2,
            R.drawable.notepad_ideas
        )

        private val funny = listOf(
            R.drawable.fun_1,
            R.drawable.fun_2,
            R.drawable.fun_4,
            R.drawable.friends_smile,
            R.drawable.friend_guys,
            R.drawable.fun_5
        )

        private val prayer = listOf(
            R.drawable.man_islam,
            R.drawable.mosque_islam,
            R.drawable.prayer_1,
            R.drawable.islam_madina,
            R.drawable.prayer_2,
            R.drawable.islam_masjid
        )

        private val allPics =
            listOf(
                wisdom,
                friendship,
                sadness,
                islam,
                other,
                morning,
                afternoon,
                love,
                funny,
                winter,
                proverbs,
                islam,
                islam.shuffled(),
                prayer
            )

        fun categoryLimit() = allPics.size
        fun picsLimit() = size

        // returns a list of pictures ids for a category
        fun getPics(category: Category) =
            allPics[categories.indexOf(category)]

        // returns a random pic from any category
        fun getRandomPic() =
            allPics[Random.nextInt(categoryLimit())][Random.nextInt(picsLimit())]

        fun getRandomPic(category: Category) =
            allPics[categories.indexOf(category)][Random.nextInt(picsLimit())]

        // returns an image via its indices
        fun getIndexPic(categoryIndex: Int, picIndex: Int) =
            allPics[categoryIndex][picIndex]

        fun getAllPics() = allPics
    }

}


class QuoteItem(
    private val text: String,
    private val category: Category
) :

    Item<ViewHolder>() {
    override fun getLayout() =
        R.layout.quote_item

    private val pic = getPics(category)[Random.nextInt(CategoryPics.size)]

    private val model = LocalQuotesFragment.favoritesViewModel


    override fun bind(viewHolder: ViewHolder, position: Int) {
        // used instead of viewHolder.itemView.etc
        val view = viewHolder.itemView
        val context = view.context
        // set text and background picture.
        view.quote_text_tv.text = context.highlightText(text)
        view.text_image.setImageResource(pic)

        // set full heart if item is already in favorites.

        if (isFavorite(text.hashCode()))
            view.quote_favorite_btn.setImageResource(R.drawable.ic_favorite)
        else
            view.quote_favorite_btn.setImageResource(R.drawable.ic_favorite_empty)

        tintFavIcon(view.quote_favorite_btn)
        view.quote_download_btn.setOnClickListener {
            val bitmap = view.quote_item_layout.getBitmapFromView()
            saveImage(bitmap, context)
        }

        view.quote_decorate_btn.setOnClickListener {
            it.findNavController()
                .navigate(LocalQuotesFragmentDirections.decorateTextNow(text, category))
        }

        // listens to button clicks and calls a specific function!
        listenButtons(
            context, listOf<View>(
                view.quote_share_btn,
                view.quote_whats_share_btn,
                view.quote_favorite_btn,
                view.quote_content_btn
            )
        )

    }

    private fun listenButtons(context: Context, views: List<View>) {
        // does the appropriate action depending on which button was clicked!
        for (button in views)
            button.setOnClickListener {
                when (button.id) {

                    R.id.quote_share_btn ->
                        shareText(text, context)


                    R.id.quote_whats_share_btn ->
                        shareWhatsApp(text, context)


                    R.id.quote_favorite_btn ->
                        fav(it as AppCompatImageView)

                    R.id.quote_content_btn ->
                        copyItem(context, text, button as AppCompatImageView)


                }
            }
    }

    companion object {
        // copies item to clipboard and shows a message!
        fun copyItem(context: Context, text: String, button: AppCompatImageView) {
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
        fun shareText(text: String, context: Context) {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, text)
            sendIntent.type = "text/plain"
            context.startActivity(sendIntent)
        }

        // if whatsApp exists, share text to it.
        fun shareWhatsApp(text: String, context: Context) {
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


        fun saveImage(bitmap: Bitmap, context: Context) {
            val uuid = UUID.randomUUID().toString().subSequence(0, 10)
            MediaStore.Images.Media.insertImage(
                context.contentResolver,
                bitmap,
                context.getString(R.string.app_name) + uuid,
                context.getString(R.string.app_name)
            )
            context.showMessage(context.getString(R.string.image_saved))
        }

        fun tintFavIcon(imageView: AppCompatImageView) {
            imageView.context.apply {
                getSharedPreferences("UserPrefs", 0).also {
                    val color = ContextCompat.getColor(
                        this@apply,
                        it.getInt("mainColor", StatusColor.Blue.value)
                    )
                    imageView.setColorFilter(color, PorterDuff.Mode.SRC_IN)
                }
            }
        }

    }

    private fun fav(favButton: AppCompatImageView) {

        val thisFavorite = Favorite(
            category = category,
            text = text,
            hashcode = text.hashCode()
        )

        if (isFavorite(text.hashCode())) {
            favButton.setImageResource(R.drawable.ic_favorite_empty)
            model.remove(thisFavorite)
        } else {
            favButton.setImageResource(R.drawable.ic_favorite)
            model.insert(thisFavorite)
        }

    }

    private fun isFavorite(code: Int) = LocalQuotesFragment.favoritesCodes.contains(code)


}