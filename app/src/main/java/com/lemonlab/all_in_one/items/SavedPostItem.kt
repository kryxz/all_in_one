package com.lemonlab.all_in_one.items

import androidx.navigation.findNavController
import com.lemonlab.all_in_one.ForumFragment
import com.lemonlab.all_in_one.ForumFragmentDirections
import com.lemonlab.all_in_one.R
import com.lemonlab.all_in_one.model.ForumPost
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.saved_post_item.view.*

class SavedPostItem(
    private val forumPost: ForumPost,
    private val adapter: GroupAdapter<ViewHolder>
) :
    Item<ViewHolder>() {

    override fun getLayout() = R.layout.saved_post_item

    private val postsViewModel = ForumFragment.postsViewModel

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val view = viewHolder.itemView

        view.saved_post_item_title.text = forumPost.title



        view.saved_post_item_title.setOnClickListener {
            it.findNavController()
                .navigate(ForumFragmentDirections.forumToThisPost(forumPost.postID))
        }

        view.saved_post_item_delete.setOnClickListener {
            postsViewModel.removePost(forumPost.postID)
            adapter.remove(this)
            adapter.notifyDataSetChanged()
        }
    }



}