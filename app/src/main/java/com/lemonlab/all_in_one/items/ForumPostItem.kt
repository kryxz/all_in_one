package com.lemonlab.all_in_one.items

import android.content.Context
import android.graphics.PorterDuff
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.lemonlab.all_in_one.ForumFragment
import com.lemonlab.all_in_one.ForumFragmentDirections
import com.lemonlab.all_in_one.R
import com.lemonlab.all_in_one.extensions.showMessage
import com.lemonlab.all_in_one.extensions.showYesNoDialog
import com.lemonlab.all_in_one.model.ForumPost
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.forum_post_item.view.*


class ForumPostItem(
    private val forumPost: ForumPost
) :
    Item<ViewHolder>() {


    override fun getLayout() =
        R.layout.forum_post_item


    private val postsViewModel = ForumFragment.postsViewModel

    private val thisUserID = postsViewModel.getUserID()

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val view = viewHolder.itemView
        val context = view.context
        view.forum_post_card.setOnClickListener {
            it.findNavController()
                .navigate(ForumFragmentDirections.forumToThisPost(forumPost.postID))
        }

        val senderID = forumPost.userID


        postsViewModel.getSenderName(senderID).observe(ForumFragment.lifecycleOwner, Observer {
            view.forum_post_item_user_image_title.text = it
        })

        with(view) {
            // add … to text

            val previewText = if (forumPost.text.length > 64)
                StringBuilder().append(forumPost.text.substring(0, 63)).append("…").toString()
            else
                forumPost.text

            // set texts
            forum_post_item_text.text = previewText
            forum_post_item_title.text = forumPost.title

            if (ForumFragment.savedPosts.contains(forumPost.postID))
                forum_post_item_save.setImageResource(R.drawable.ic_bookmark)

            // set likes and dislikes count
            forum_post_item_dislike.setText(forumPost.dislikesCount().toString())
            forum_post_item_like.setText(forumPost.likesCount().toString())

            updateUILikes(
                context = context,
                thisUserID = thisUserID,
                forumPost = forumPost,
                like = forum_post_item_like,
                dislike = forum_post_item_dislike
            )

            // like and dislike functions
            forum_post_item_dislike.setOnClickListener {
                forumPost.disLike(thisUserID)
                updateUILikes(
                    context = context,
                    thisUserID = thisUserID,
                    forumPost = forumPost,
                    like = forum_post_item_like,
                    dislike = forum_post_item_dislike
                )
            }
            forum_post_item_like.setOnClickListener {
                forumPost.like(thisUserID)
                updateUILikes(
                    context = context,
                    thisUserID = thisUserID,
                    forumPost = forumPost,
                    like = forum_post_item_like,
                    dislike = forum_post_item_dislike
                )
            }


            // report and save
            buttonsListener(
                context, listOf(
                    forum_post_item_save,
                    forum_post_item_report
                )
            )

            // delete function logic
            forum_post_item_delete.apply {
                // return if user isn't the owner of post.
                if (thisUserID != forumPost.userID) return@apply

                this.visibility = View.VISIBLE

                fun deletePost() {
                    forumPost.deletePost()
                    postsViewModel.removePost(forumPost.postID)
                    context.showMessage(context.getString(R.string.post_deleted))
                }


                this.setOnClickListener {
                    context.showYesNoDialog(
                        functionToPerform = ::deletePost, functionIfCancel = {},
                        dialogTitle = context.getString(R.string.delete_post),
                        dialogMessage = context.getString(R.string.are_you_sure)
                    )

                }
            }
        }


    }


    private fun savePost(saveButton: AppCompatImageView) {
        val postID = forumPost.postID
        if (ForumFragment.savedPosts.contains(forumPost.postID)) {
            postsViewModel.removePost(postID)
            saveButton.setImageResource(R.drawable.ic_bookmark_border)
        } else {
            postsViewModel.savePost(postID)
            saveButton.setImageResource(R.drawable.ic_bookmark)
        }


    }

    private fun buttonsListener(context: Context, buttons: List<View>) {
        // sends report to database. does nothing if exists.
        fun addReport() {
            forumPost.report(thisUserID)
            context.showMessage(context.getString(R.string.report_sent))

        }

        for (button in buttons) {
            button.setOnClickListener {
                when (button.id) {
                    R.id.forum_post_item_save -> {
                        savePost(it as AppCompatImageView)
                    }

                    R.id.forum_post_item_report -> {
                        // shows report dialog.
                        context.showYesNoDialog(
                            ::addReport,
                            {},
                            context.getString(R.string.report_post),
                            context.getString(R.string.report_post_confirm)
                        )

                    }
                }
            }
        }
    }


    companion object {
        private fun tintDrawable(
            context: Context,
            editText: AppCompatEditText,
            drawableID: Int,
            colorID: Int
        ) {
            var drawable = ContextCompat.getDrawable(context, drawableID)
            drawable = DrawableCompat.wrap(drawable!!)
            DrawableCompat.setTint(drawable, ContextCompat.getColor(context, colorID))
            DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN)
            editText.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)

        }

        fun updateUILikes(
            forumPost: ForumPost,
            thisUserID: String,
            context: Context,
            like: AppCompatEditText,
            dislike: AppCompatEditText
        ) {
            when {
                forumPost.likedBy(thisUserID) -> {
                    tintDrawable(context, like, R.drawable.ic_arrow_upward, R.color.darkGreen)
                    tintDrawable(
                        context,
                        dislike,
                        R.drawable.ic_arrow_downward,
                        R.color.colorPrimaryDark
                    )

                }
                forumPost.dislikedBy(thisUserID) -> {
                    tintDrawable(context, dislike, R.drawable.ic_arrow_downward, R.color.darkRed)
                    tintDrawable(
                        context,
                        like,
                        R.drawable.ic_arrow_upward,
                        R.color.colorPrimaryDark
                    )

                }
                else -> {
                    tintDrawable(
                        context,
                        dislike,
                        R.drawable.ic_arrow_downward,
                        R.color.colorPrimaryDark
                    )
                    tintDrawable(
                        context,
                        like,
                        R.drawable.ic_arrow_upward,
                        R.color.colorPrimaryDark
                    )
                }
            }


        }
    }


}