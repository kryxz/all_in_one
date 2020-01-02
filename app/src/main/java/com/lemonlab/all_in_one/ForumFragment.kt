package com.lemonlab.all_in_one


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.lemonlab.all_in_one.extensions.checkUser
import com.lemonlab.all_in_one.items.ForumPostItem
import com.lemonlab.all_in_one.model.Comment
import com.lemonlab.all_in_one.model.ForumPost
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_forum.*
import java.util.*
import kotlin.collections.ArrayList


/*
    Users Forum. Users can start discussions, post comments and more.
 */


class ForumFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forum, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.checkUser()
        createPost()
        getPosts()
        super.onViewCreated(view, savedInstanceState)
    }


    private fun createPost() {
        create_post_btn.setOnClickListener {
            it.findNavController().navigate(ForumFragmentDirections.createNewPost())
        }

    }

    private fun getPosts() {
        val adapter = GroupAdapter<ViewHolder>()
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("posts").orderBy("timestamp")
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }

            if (context == null) return@addSnapshotListener

            if (snapshot != null) {
                // clear the adapter
                adapter.clear()
                // get all messages and clear the old
                val documents = snapshot.documents
                for (doc in documents) {
                    val title = doc.data!!["title"].toString()
                    val text = doc.data!!["text"].toString()
                    val userID = doc.data!!["userID"].toString()
                    val timestamp = doc.get("timestamp", Date::class.java)!!

                    val comments: ArrayList<Comment>? = if (doc.data!!["comments"] != null)
                        doc.get("comments") as ArrayList<Comment>
                    else
                        ArrayList()


                    val likesIDs: ArrayList<String>? =
                        if (doc.data!!["likesIDs"] != null)
                            doc.get("likesIDs")!! as ArrayList<String>
                        else
                            ArrayList()

                    val dislikesIDs: ArrayList<String>? =
                        if (doc.data!!["dislikesIDs"] != null)
                            doc.get("dislikesIDs")!! as ArrayList<String>
                        else
                            ArrayList()

                    val postID = doc.id
                    val likes = doc.data!!["likes"].toString().toInt()
                    val dislikes = doc.data!!["dislikes"].toString().toInt()
                    val post = ForumPost(
                        title = title,
                        text = text,
                        userID = userID,
                        timestamp = timestamp,
                        comments = comments,
                        likesIDs = likesIDs,
                        dislikesIDs = dislikesIDs,
                        likes = likes,
                        dislikes = dislikes
                    )
                    adapter.add(ForumPostItem(post, context!!, postID))
                }
                if (view != null)
                    forum_rv.adapter = adapter

            }
        }
    }
}
