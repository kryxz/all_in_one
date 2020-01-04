package com.lemonlab.all_in_one.items

import android.content.Context
import androidx.navigation.findNavController
import com.lemonlab.all_in_one.ForumFragmentDirections
import com.lemonlab.all_in_one.R
import com.lemonlab.all_in_one.extensions.showYesNoDialog
import com.lemonlab.all_in_one.model.ForumPost
import com.lemonlab.all_in_one.model.SavedPosts
import com.lemonlab.all_in_one.model.SavedPostsRoomDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.saved_post_item.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class SavedPost(
    private val forumPost: ForumPost,
    private val context: Context,
    private val postID: String,
    private val adapter: GroupAdapter<ViewHolder>
) :
    Item<ViewHolder>() {

    override fun getLayout() = R.layout.saved_post_item


    override fun bind(viewHolder: ViewHolder, position: Int) {
        val view = viewHolder.itemView

        view.saved_post_item_title.text = forumPost.title



        view.saved_post_item_title.setOnClickListener {
            it.findNavController().navigate(ForumFragmentDirections.forumToThisPost(postID))
        }

        view.saved_post_item_delete.setOnClickListener {
            showDialog()
        }
    }


    private fun showDialog() {
        context.showYesNoDialog(
            {
                GlobalScope.launch {
                    val savedPostsDao =
                        SavedPostsRoomDatabase.getDatabase(context).SavedPostsDao()
                    savedPostsDao.deletePost(SavedPosts(postID))
                    this.cancel()
                }
                adapter.remove(this@SavedPost)
                adapter.notifyDataSetChanged()
            },
            {},
            context.getString(R.string.delete_post_favorites),
            context.getString(R.string.are_you_sure)
        )
    }

}