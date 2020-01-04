@file:Suppress("UNCHECKED_CAST")

package com.lemonlab.all_in_one


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lemonlab.all_in_one.extensions.showMessage
import com.lemonlab.all_in_one.extensions.showYesNoDialog
import com.lemonlab.all_in_one.model.Comment
import com.lemonlab.all_in_one.model.ForumPost
import com.lemonlab.all_in_one.model.SavedPost
import com.lemonlab.all_in_one.model.SavedPostsRoomDatabase
import kotlinx.android.synthetic.main.fragment_view_post.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

/**
 * A fragment to view the actual post and its comments, likes, etc.
 */


class ViewPostFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val postID = ViewPostFragmentArgs.fromBundle(arguments!!).postID
        getPostData(postID)
        super.onViewCreated(view, savedInstanceState)
    }

    private fun getPostData(postID: String) {
        val postRef = FirebaseFirestore.getInstance().collection("posts").document(postID)

        postRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }

            if (context == null ||
                snapshot == null
            ) return@addSnapshotListener

            if (snapshot.data == null) {
                setDeleted()
                return@addSnapshotListener
            }
            val title = snapshot.data!!["title"].toString()
            val text = snapshot.data!!["text"].toString()
            val userID = snapshot.data!!["userID"].toString()
            val timestamp = snapshot.get("timestamp", Date::class.java)!!

            val comments: ArrayList<Comment>? = if (snapshot.data!!["comments"] != null)
                snapshot.get("comments")!! as ArrayList<Comment>
            else
                ArrayList()

            val likesIDs: ArrayList<String>? =
                if (snapshot.data!!["likesIDs"] != null)
                    snapshot.get("likesIDs")!! as ArrayList<String>
                else
                    ArrayList()

            val dislikesIDs: ArrayList<String>? =
                if (snapshot.data!!["dislikesIDs"] != null)
                    snapshot.get("dislikesIDs")!! as ArrayList<String>
                else
                    ArrayList()

            val reportIDs: ArrayList<String>? =
                if (snapshot.data!!["reportIDs"] != null)
                    snapshot.get("reportIDs")!! as ArrayList<String>
                else
                    ArrayList()

            //    val likes = snapshot.data!!["likes"].toString().toInt()
            //   val dislikes = snapshot.data!!["dislikes"].toString().toInt()
            val reports = snapshot.data!!["reports"].toString().toInt()
            val thePost = ForumPost(
                title = title,
                text = text,
                userID = userID,
                timestamp = timestamp,
                comments = comments,
                dislikesIDs = dislikesIDs,
                likesIDs = likesIDs,
                //     likes = likes,
                //     dislikes = dislikes,
                reports = reports,
                reportIDs = reportIDs,
                postID = snapshot.id
            )

            // update ui
            setData(thePost)


            init(thePost)
        }

    }

    private fun init(post: ForumPost) {
        var savedPosts = listOf<String>()

        // get saved posts.
        GlobalScope.launch {
            val savedPostsDao = SavedPostsRoomDatabase.getDatabase(context!!).SavedPostsDao()
            savedPosts = savedPostsDao.getSavedPosts()
            this.coroutineContext.cancel()
        }
        fun deletePost() {
            GlobalScope.launch {
                val savedPostsDao = SavedPostsRoomDatabase.getDatabase(context!!).SavedPostsDao()
                savedPostsDao.deletePost(SavedPost(post.postID))
                savedPosts = savedPostsDao.getSavedPosts()
                this.coroutineContext.cancel()
            }
            view_post_save.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(
                    context!!,
                    R.drawable.ic_bookmark_border
                ), null, null, null
            )
        }

        fun savePost() {
            GlobalScope.launch {
                val savedPostsDao = SavedPostsRoomDatabase.getDatabase(context!!).SavedPostsDao()
                savedPostsDao.insertPost(SavedPost(post.postID))
                savedPosts = savedPostsDao.getSavedPosts()
                this.coroutineContext.cancel()
            }
            view_post_save.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(
                    context!!,
                    R.drawable.ic_bookmark
                ), null, null, null
            )
        }

        view_post_save.setOnClickListener {
            if (savedPosts.contains(post.postID))
                deletePost()
            else
                savePost()

        }

        if (savedPosts.contains(post.postID))
            view_post_save.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(
                    context!!,
                    R.drawable.ic_bookmark
                ), null, null, null
            )

        val thisUserID = FirebaseAuth.getInstance().uid.toString()

        // checks if post should be deleted.
        checkReports(post)


        // button click listeners. Updates post in database.
        view_post_send_comment_btn.setOnClickListener {
            if (view_post_comment_text.text.isNullOrEmpty()) return@setOnClickListener
            sendComment(post)
        }

        view_post_dislike.setOnClickListener {
            // if disliked, cancel dislike
            post.disLike(thisUserID)
        }


        view_post_like.setOnClickListener {
            // if liked, cancel like
            post.like(thisUserID)

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
        post_view_item_title.text = post.title
        post_view_item_text.text = post.text
        view_post_postedWhen.text = post.timestamp.toString()
        view_post_dislike.setText(post.dislikesIDs!!.size.toString())
        view_post_like.setText(post.likesIDs!!.size.toString())


        // get user name from fireStore
        val userRef = FirebaseFirestore.getInstance().collection("users").document(post.userID)
        // get name only once.
        userRef.get().addOnSuccessListener {
            if (context == null || it == null || view == null) return@addOnSuccessListener

            view_post_postedBy.text = it.data!!["name"].toString()

        }

        with(view_post_delete) {
            val currentUserUid = FirebaseAuth.getInstance().uid
            if (currentUserUid != post.userID) return
            fun deletePost() {
                FirebaseFirestore.getInstance().collection("posts").document(post.postID)
                    .delete()
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

    }

    private fun checkReports(post: ForumPost) {
        val maxReports = 5
        if (post.reports >= maxReports) {
            FirebaseFirestore.getInstance().collection("posts").document(post.postID)
                .delete()
        }
    }

    private fun sendComment(post: ForumPost) {

        // add the comment to fireStore

        val currentUserUid = FirebaseAuth.getInstance().uid

        val comment = Comment(
            text = view_post_comment_text.text.toString(),
            userID = currentUserUid!!
        )

        val db = FirebaseFirestore.getInstance()
        post.comments!!.add(comment)

        // update the document
        db.collection("posts").document(post.postID).set(post)

    }

}
