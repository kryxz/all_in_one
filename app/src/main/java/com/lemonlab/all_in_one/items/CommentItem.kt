package com.lemonlab.all_in_one.items

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.lemonlab.all_in_one.R
import com.lemonlab.all_in_one.ViewPostFragment
import com.lemonlab.all_in_one.extensions.getDateAsString
import com.lemonlab.all_in_one.extensions.showMessage
import com.lemonlab.all_in_one.extensions.showYesNoDialog
import com.lemonlab.all_in_one.model.Comment
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.comment_item.view.*

class CommentItem(private val comment: Comment) :

    Item<ViewHolder>() {

    private val postsViewModel = ViewPostFragment.postsViewModel

    private val thisUserID = postsViewModel.getUserID()

    override fun getLayout() = R.layout.comment_item

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val view = viewHolder.itemView


        // set comment text, copy text on long click
        with(view.comment_text) {
            text = comment.text
            setOnLongClickListener {
                val clipboard =
                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("text", text)
                clipboard.setPrimaryClip(clip)
                context.showMessage(context.getString(R.string.comment_copied))
                true
            }
        }

        // report this comment
        view.comment_report_button.setOnClickListener {
            it.context.showYesNoDialog(
                { comment.report(thisUserID) },
                {},
                it.context.getString(R.string.report_comment),
                it.context.getString(R.string.report_comment_confirm)
            )
        }

        view.comment_postedWhen.text = getDateAsString(comment.timestamp)


        // show delete button if user is comment sender!
        if (thisUserID == comment.userID)
            with(view.comment_delete_button) {
                visibility = View.VISIBLE
                setOnClickListener {
                    it.context.showYesNoDialog(
                        { comment.deleteComment() },
                        {},
                        context.getString(R.string.delete_comment),
                        context.getString(R.string.delete_comment_confirm)
                    )
                }
            }

        postsViewModel.getSenderName(comment.userID)
            .observe(ViewPostFragment.lifecycleOwner, Observer<String> {
                view.comment_username.text = it
                postsViewModel.getSenderName(comment.userID)
                    .removeObservers(ViewPostFragment.lifecycleOwner)
            })


        //  hide comment text when username is clicked!
        with(view.comment_username) {
            if (comment.userID == thisUserID)
                setTextColor(ContextCompat.getColor(context, R.color.darkGreen))
            else
                setTextColor(ContextCompat.getColor(context, R.color.darkBlue))

            setOnClickListener {
                val visibility = view.comment_details_view.visibility
                view.comment_details_view.visibility = if (visibility == View.GONE)
                    View.VISIBLE
                else
                    View.GONE
            }
        }


    }

}