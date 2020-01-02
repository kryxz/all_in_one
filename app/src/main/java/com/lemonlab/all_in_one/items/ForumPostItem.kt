package com.lemonlab.all_in_one.items

import android.content.Context
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.lemonlab.all_in_one.ForumFragmentDirections
import com.lemonlab.all_in_one.R
import com.lemonlab.all_in_one.model.ForumPost
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.forum_post_item.view.*

class ForumPostItem(
    private val forumPost: ForumPost,
    private val context: Context,
    private val postID: String
) :
    Item<ViewHolder>() {


    override fun getLayout() =
        R.layout.forum_post_item

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val view = viewHolder.itemView
        val uid = FirebaseAuth.getInstance().uid.toString()
        view.setOnClickListener {
            it.findNavController().navigate(ForumFragmentDirections.forumToThisPost(postID))
        }
        with(view) {

            // add … to text

            val previewText = if (forumPost.text.length > 64)
                StringBuilder().append(forumPost.text.substring(0, 63)).append("…").toString()
            else
                forumPost.text


            // set texts
            forum_post_item_text.text = previewText
            forum_post_item_title.text = forumPost.title

            // show delete button if user is the owner of the post
            if (uid == forumPost.userID) {
                forum_post_item_delete.visibility = View.VISIBLE
            }
            buttonsListener(
                listOf<AppCompatImageView>(
                    forum_post_item_save,
                    forum_post_item_delete,
                    forum_post_item_like,
                    forum_post_item_dislike,
                    forum_post_item_report
                )
            )
        }


    }

    private fun buttonsListener(buttons: List<AppCompatImageView>) {

        for (button in buttons) {
            button.setOnClickListener {
                when (button.id) {
                    // TODO implement these functions
                    R.id.forum_post_item_save -> {
                    }
                    R.id.forum_post_item_delete -> {
                    }
                    R.id.forum_post_item_like -> {
                    }
                    R.id.forum_post_item_dislike -> {
                    }
                    R.id.forum_post_item_report -> {
                    }
                }
            }
        }
    }


}