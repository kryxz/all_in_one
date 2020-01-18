@file:Suppress("UNCHECKED_CAST")

package com.lemonlab.all_in_one


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lemonlab.all_in_one.extensions.getDateAsString
import com.lemonlab.all_in_one.extensions.hideKeypad
import com.lemonlab.all_in_one.extensions.showMessage
import com.lemonlab.all_in_one.extensions.showYesNoDialog
import com.lemonlab.all_in_one.items.CommentItem
import com.lemonlab.all_in_one.items.ForumPostItem
import com.lemonlab.all_in_one.model.Comment
import com.lemonlab.all_in_one.model.ForumPost
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_view_post.*
import java.sql.Timestamp

/**
 * A fragment to view the actual post and its comments, likes, etc.
 */


class ViewPostFragment : Fragment() {


    companion object {
        lateinit var lifecycleOwner: LifecycleOwner
        lateinit var postsViewModel: UsersTextsViewModel
        var savedPosts: List<String> = listOf()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun initAdapter(postID: String) {
        lifecycleOwner = viewLifecycleOwner
        val adapter = GroupAdapter<ViewHolder>()
        view_post_comments_rv.adapter = adapter

        postsViewModel.getPostComments(postID).observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer
            if (it.isEmpty()) {
                no_comments_text.visibility = View.VISIBLE
                view_post_comments_rv.visibility = View.GONE
            } else {
                no_comments_text.visibility = View.GONE
                view_post_comments_rv.visibility = View.VISIBLE
            }

            adapter.clear()
            for (comment in it)
                adapter.add(CommentItem(comment))

        })

    }

    private fun init() {

        postsViewModel = ViewModelProviders.of(this)[UsersTextsViewModel::class.java]
        val postID = ViewPostFragmentArgs.fromBundle(arguments!!).postID

        viewPostFragmentView.visibility = View.GONE
        postLoadingProgressBar.visibility = View.VISIBLE

        val observer = androidx.lifecycle.Observer<ForumPost> {
            if (it == null)
                setDeleted()
            else
                setData(it)

        }

        if (postsViewModel.savedPostsIDs.value != null)
            savedPosts = postsViewModel.savedPostsIDs.value!!

        postsViewModel.getPost(postID).observe(this, observer)
        postsViewModel.savedPostsIDs.observe(this, Observer {
            savedPosts = it
        })

        val saved = R.drawable.ic_bookmark


        if (savedPosts.contains(postID))
            view_post_save.setImageResource(saved)

        initAdapter(postID)
    }


    private fun setDeleted() {
        // clear text
        post_view_item_title.text = getString(R.string.post_deleted)
        post_view_item_text.text = getString(R.string.post_deleted)

        // hide user name
        view_post_postedBy.text = getString(R.string.post_deleted)

        view_post_postedWhen.text = ""
        // disable all buttons
        view_post_dislike.setOnClickListener(null)
        view_post_like.setOnClickListener(null)
        view_post_delete.setOnClickListener(null)
        view_post_report.setOnClickListener(null)
        view_post_save.setOnClickListener(null)

        // disable comments
        view_post_send_comment_btn.setOnClickListener(null)
        view_post_comment_text.isEnabled = false
    }

    private fun setData(post: ForumPost) {

        val postID = post.postID
        post_view_item_title.text = post.title
        post_view_item_text.text = post.text
        view_post_postedWhen.text = getDateAsString(post.timestamp)
        view_post_dislike.setText(post.dislikesCount().toString())
        view_post_like.setText(post.likesCount().toString())

        // set bookmarked if post is saved!
        val saved = R.drawable.ic_bookmark
        val notSaved = R.drawable.ic_bookmark_border
        val thisUserID = postsViewModel.getUserID()

        fun deletePost() {
            postsViewModel.removePost(postID)
            view_post_save.setImageResource(notSaved)
        }

        fun savePost() {
            postsViewModel.savePost(postID)
            view_post_save.setImageResource(saved)

        }

        fun updateLikesUI() {
            ForumPostItem.updateUILikes(
                forumPost = post,
                context = context!!,
                like = view_post_like, dislike = view_post_dislike,
                thisUserID = thisUserID
            )
        }

        view_post_save.setOnClickListener {
            if (savedPosts.contains(postID)) deletePost()
            else savePost()

        }

        // get user name from fireStore
        val userRef = FirebaseFirestore.getInstance().collection("users").document(post.userID)
        // get name only once.
        userRef.get().addOnSuccessListener {
            if (context == null || it == null || view == null) return@addOnSuccessListener
            viewPostFragmentView.visibility = View.VISIBLE
            postLoadingProgressBar.visibility = View.GONE
            if (it.data == null) {
                view_post_postedBy.text = getString(R.string.username)
                return@addOnSuccessListener
            }
            view_post_postedBy.text = it.data!!["name"].toString()

        }

        with(view_post_delete) {
            val currentUserUid = FirebaseAuth.getInstance().uid
            if (currentUserUid != post.userID) return@with
            fun deletePost() {
                post.deletePost()
                view!!.findNavController().navigateUp()
                context.showMessage(getString(R.string.post_deleted))
            }
            visibility = View.VISIBLE
            setOnClickListener {

                context.showYesNoDialog(
                    functionToPerform = ::deletePost, functionIfCancel = {},
                    dialogTitle = getString(R.string.delete_post),
                    dialogMessage = getString(R.string.are_you_sure)
                )

            }
        }


        // checks if post should be deleted.
        post.checkReports()


        // button click listeners. Updates post in database.
        view_post_send_comment_btn.setOnClickListener {
            if (view_post_comment_text.text.isNullOrEmpty()) return@setOnClickListener
            sendComment(post)
        }


        updateLikesUI()

        view_post_dislike.setOnClickListener {
            // if disliked, cancel dislike
            post.disLike(thisUserID)
            updateLikesUI()

        }


        view_post_like.setOnClickListener {
            // if liked, cancel like
            post.like(thisUserID)
            updateLikesUI()

        }

        view_post_report.setOnClickListener {
            // shows a yes/no dialog.
            context!!.showYesNoDialog(
                {
                    post.report(thisUserID)
                    context!!.showMessage(getString(R.string.report_sent))
                },
                {},
                getString(R.string.report_post),
                getString(R.string.report_post_confirm)
            )
        }

    }

    private fun sendComment(post: ForumPost) {

        // add the comment to fireStore


        val comment = Comment(
            text = view_post_comment_text.text.toString(),
            postID = post.postID,
            reportIDs = ArrayList(),
            userID = postsViewModel.getUserID(),
            timestamp = Timestamp(System.currentTimeMillis())

        )
        view_post_comment_text.text!!.clear()
        post.sendComment(comment)
        activity!!.hideKeypad()

    }

}
