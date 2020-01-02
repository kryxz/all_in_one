package com.lemonlab.all_in_one


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lemonlab.all_in_one.model.Comment
import com.lemonlab.all_in_one.model.ForumPost
import kotlinx.android.synthetic.main.fragment_view_post.*
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

            if (context == null || snapshot == null) return@addSnapshotListener
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

            val likes = snapshot.data!!["likes"].toString().toInt()
            val dislikes = snapshot.data!!["dislikes"].toString().toInt()
            val post = ForumPost(
                title = title,
                text = text,
                userID = userID,
                timestamp = timestamp,
                comments = comments,
                dislikesIDs = dislikesIDs,
                likesIDs = likesIDs,
                likes = likes,
                dislikes = dislikes
            )
            setData(post)
            likesDislikes(post, postID)
        }

    }

    private fun likesDislikes(post: ForumPost, postID: String) {
        val postRef = FirebaseFirestore.getInstance().collection("posts").document(postID)
        val thisUserID = FirebaseAuth.getInstance().uid.toString()

        fun removeLike() {
            post.likes -= 1
            post.likesIDs!!.remove(thisUserID)
        }

        fun removeDislike() {
            post.dislikes -= 1
            post.dislikesIDs!!.remove(thisUserID)
        }

        fun addLike() {
            post.likes += 1
            post.likesIDs!!.add(thisUserID)
        }

        fun addDislike() {
            post.dislikes += 1
            post.dislikesIDs!!.add(thisUserID)
        }

        view_post_dislike.setOnClickListener {
            // if disliked, cancel dislike
            when {
                post.dislikesIDs!!.contains(thisUserID) -> removeDislike()
                post.likesIDs!!.contains(thisUserID) -> removeLike()
                else -> addDislike()
            }

            postRef.set(post)
        }


        view_post_like.setOnClickListener {
            // if liked, cancel like
            when {
                post.likesIDs!!.contains(thisUserID) -> removeLike()
                post.dislikesIDs!!.contains(thisUserID) -> removeDislike()
                else -> addLike()
            }

            postRef.set(post)
        }

    }

    private fun setData(post: ForumPost) {
        post_view_item_title.text = post.title
        post_view_item_text.text = post.text
        view_post_postedWhen.text = post.timestamp.toString()
        view_post_dislike.setText(post.dislikes.toString())
        view_post_like.setText(post.likes.toString())

        // TODO change view_post_save icon to ic_bookmark if user already saved the post.
        // TODO add buttons functionality

        // get user name from fireStore
        val userRef = FirebaseFirestore.getInstance().collection("users").document(post.userID)
        userRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }

            if (context == null || snapshot == null) return@addSnapshotListener

            view_post_postedBy.text = snapshot.data!!["name"].toString()

        }
    }

}
