package com.lemonlab.all_in_one.items

import android.content.Context
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lemonlab.all_in_one.ForumFragmentDirections
import com.lemonlab.all_in_one.R
import com.lemonlab.all_in_one.extensions.recreateFragment
import com.lemonlab.all_in_one.extensions.showMessage
import com.lemonlab.all_in_one.extensions.showYesNoDialog
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


            // delete function logic
            with(forum_post_item_delete) {
                // return if user isn't the owner of post.
                val currentUserUid = FirebaseAuth.getInstance().uid
                if (currentUserUid != forumPost.userID) return


                this.visibility = View.VISIBLE

                fun deletePost() {
                    FirebaseFirestore.getInstance().collection("posts").document(postID)
                        .delete()

                    // recreate fragment to refresh items
                    recreateFragment(R.id.forumFragment)

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
            buttonsListener(
                listOf<AppCompatImageView>(
                    forum_post_item_save,
                    forum_post_item_like,
                    forum_post_item_dislike,
                    forum_post_item_report
                )
            )
        }


    }


    private fun buttonsListener(buttons: List<AppCompatImageView>) {
        val thisUserID = FirebaseAuth.getInstance().uid.toString()
        // sends report to database. does nothing if exists.
        fun addReport() {
            forumPost.report(thisUserID)
            context.showMessage(context.getString(R.string.report_sent))

        }

        for (button in buttons) {
            button.setOnClickListener {
                when (button.id) {
                    // TODO implement this function
                    R.id.forum_post_item_save -> {

                    }

                    // like button
                    R.id.forum_post_item_like -> {
                        forumPost.like(thisUserID)
                    }

                    // dislike button
                    R.id.forum_post_item_dislike -> {
                        forumPost.disLike(thisUserID)
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


}