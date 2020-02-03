package com.lemonlab.all_in_one.items

import android.content.Context
import android.graphics.PorterDuff
import android.os.Handler
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.lemonlab.all_in_one.NotificationSender
import com.lemonlab.all_in_one.R
import com.lemonlab.all_in_one.UsersTextsFragment
import com.lemonlab.all_in_one.UsersTextsFragmentDirections
import com.lemonlab.all_in_one.extensions.*
import com.lemonlab.all_in_one.model.Favorite
import com.lemonlab.all_in_one.model.UserStatus
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.user_status_item.view.*


class UserStatusItem(private val userStatus: UserStatus) :

    Item<ViewHolder>() {


    override fun getLayout() =
        R.layout.user_status_item

    private val text = userStatus.text

    private val showImage = UsersTextsFragment.showImage

    private val model = UsersTextsFragment.statusesViewModel
    private val favoritesViewModel = UsersTextsFragment.favoritesViewModel

    private val randomPic = CategoryPics.getRandomPic(userStatus.category)

    private val color = userStatus.statusColor.value
    private val thisUserId = model.getUserID()
    override fun bind(viewHolder: ViewHolder, position: Int) {
        val view = viewHolder.itemView
        val context = view.context


        if (thisUserId == adminUID)
            view.setOnLongClickListener {
                context.showYesNoDialog(
                    { userStatus.delete() },
                    {},
                    context.getString(R.string.deleteStatus),
                    context.getString(R.string.report_status_confirm)
                )
                true
            }

        if (showImage) {
            view.user_status_image.setImageResource(randomPic)
            view.user_status_text.text =
                context.highlightTextWithColor(userStatus.statusColor.value, text)
        } else {
            view.user_status_text.text = text
            view.user_status_image.setBackgroundColor(ContextCompat.getColor(context, color))

        }


        // set likes count
        view.user_status_likes_text.text = userStatus.likesCount().toString()
        tintDrawable(context, view.user_status_likes_text)

        val favButton = view.user_status_favorite_btn
        model.likesCount(userStatus.statusID).observe(UsersTextsFragment.lifecycleOwner, Observer {
            if (it.second == userStatus.statusID)
                view.user_status_likes_text.text = it.first.toString()
        })


        favoritesViewModel.favoritesCodes.observe(UsersTextsFragment.lifecycleOwner, Observer {
            if (it.contains(text.hashCode()))
                favButton.setImageResource(R.drawable.ic_favorite)
            else
                favButton.setImageResource(R.drawable.ic_favorite_empty)

        })

        // set like button to look like the status text color
        favButton.setColorFilter(
            ContextCompat.getColor(
                context,
                userStatus.statusColor.value
            ), PorterDuff.Mode.SRC_IN
        )

        // set sent date text
        view.user_status_date_text.text = getDateAsString(userStatus.timestamp)


        // set status sender name

        model.getSenderName(userStatus.userID).observe(UsersTextsFragment.lifecycleOwner, Observer {
            view.user_status_username_text.text = it
        })


        // set category text
        view.user_status_category_text.text = context.getString(userStatus.category.textID)


        val buttons = listOf<View>(
            view.user_status_details_btn,
            view.user_status_download_btn,
            view.user_status_whats_share_btn,
            view.user_status_share_btn,
            view.user_status_content_btn,
            view.user_status_favorite_btn,
            view.user_status_report_btn,
            view.user_status_decorate_btn
        )

        if (userStatus.userID == thisUserId)
            view.user_status_report_btn.visibility = View.GONE

        for (button in buttons) {
            button.setOnClickListener {
                when (it.id) {
                    // shows hides status details
                    R.id.user_status_details_btn -> {
                        val visibility = view.user_status_details_layout.visibility

                        if (visibility == View.GONE) {
                            (it as AppCompatImageView).setImageResource(R.drawable.ic_expand_less)
                            view.user_status_details_layout.visibility = View.VISIBLE

                        } else {
                            (it as AppCompatImageView).setImageResource(R.drawable.ic_expand_more)
                            view.user_status_details_layout.visibility = View.GONE
                        }


                    }

                    // download status as image
                    R.id.user_status_download_btn ->
                        QuoteItem.saveImage(
                            view.user_status_item_layout.getBitmapFromView(),
                            context
                        )


                    // share to WhatsApp
                    R.id.user_status_whats_share_btn ->
                        QuoteItem.shareWhatsApp(text, context)


                    // copy text
                    R.id.user_status_content_btn ->
                        QuoteItem.copyItem(
                            context,
                            text,
                            (view.user_status_content_btn as AppCompatImageView)
                        )

                    // share as text
                    R.id.user_status_share_btn ->
                        QuoteItem.shareText(text, context)

                    // add to favorites. send like to remote database.
                    R.id.user_status_favorite_btn ->
                        favorite(context)

                    R.id.user_status_report_btn ->
                        report(context)


                    R.id.user_status_decorate_btn ->
                        decorateText(it)
                }
            }
        }


    }

    private fun decorateText(it: View) {
        val direction =
            UsersTextsFragmentDirections.decorateTextNow(text, userStatus.category)
        it.findNavController().navigate(direction)
    }

    private fun report(context: Context) {
        val auth = FirebaseAuth.getInstance()
        context.showYesNoDialog(
            {
                if (auth.currentUser != null)
                    userStatus.report(auth.uid!!)
            },
            {},
            context.getString(R.string.report_status),
            context.getString(R.string.report_status_confirm)
        )

    }

    private fun tintDrawable(context: Context, editText: AppCompatTextView) {
        var drawable = ContextCompat.getDrawable(context, R.drawable.ic_favorite)
        drawable = DrawableCompat.wrap(drawable!!)
        DrawableCompat.setTint(
            drawable,
            ContextCompat.getColor(context, userStatus.statusColor.value)
        )
        DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN)
        editText.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)

    }

    private fun favorite(context: Context) {
        val auth = FirebaseAuth.getInstance()

        val thisFavorite = Favorite(
            category = userStatus.category,
            text = text, hashcode = text.hashCode()
        )


        if (isFavorite(text.hashCode())) {
            favoritesViewModel.remove(thisFavorite)
            if (auth.currentUser != null)
                userStatus.cancelLike(auth.uid!!)

        } else {
            favoritesViewModel.insert(thisFavorite)

            if (auth.currentUser != null)
                userStatus.like(auth.uid!!)

            if (thisUserId != userStatus.userID)
                Handler().postDelayed({
                    if (userStatus.likesCount() % 4 == 0)
                        NotificationSender().notifyUserLikes(
                            context,
                            userStatus.userID,
                            userStatus.likesCount()
                        )
                }, 2000)

        }


    }

    private fun isFavorite(code: Int) = favoritesViewModel.favoritesCodes.value!!.contains(code)

}