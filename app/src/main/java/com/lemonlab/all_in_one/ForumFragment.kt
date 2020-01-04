package com.lemonlab.all_in_one


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.lemonlab.all_in_one.extensions.checkUser
import com.lemonlab.all_in_one.extensions.recreateFragment
import com.lemonlab.all_in_one.extensions.setFragmentTitle
import com.lemonlab.all_in_one.extensions.showMessage
import com.lemonlab.all_in_one.items.ForumPostItem
import com.lemonlab.all_in_one.items.SavedPostItem
import com.lemonlab.all_in_one.model.Comment
import com.lemonlab.all_in_one.model.ForumPost
import com.lemonlab.all_in_one.model.SavedPostsRoomDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_forum.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
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
        setHasOptionsMenu(true)
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

    @Suppress("UNCHECKED_CAST") // TODO:
    private fun getPosts() {
        // false unless user clicks the button in appBar.
        val seeBookmarks = ForumFragmentArgs.fromBundle(arguments!!).seeBookmarks
        var listOfSavedPosts: List<String>
        val adapter = GroupAdapter<ViewHolder>()
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("posts").orderBy("timestamp")
        if (seeBookmarks) {
            activity!!.setFragmentTitle(getString(R.string.saved_posts))
            GlobalScope.launch {
                val savedPostsDao = SavedPostsRoomDatabase.getDatabase(context!!).SavedPostsDao()
                listOfSavedPosts = savedPostsDao.getSavedPosts()
                getSaved(listOfSavedPosts, docRef)
                this.coroutineContext.cancel()
            }
        } else
            docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }

                if (context == null) return@addSnapshotListener

                if (snapshot != null && !snapshot.isEmpty) {
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
                            doc.get("comments") as ArrayList<Comment>?
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

                        val reportIDs: ArrayList<String>? =
                            if (doc.data!!["reportIDs"] != null)
                                doc.get("reportIDs")!! as ArrayList<String>
                            else
                                ArrayList()

                        val reports = doc.data!!["reports"].toString().toInt()

                        val postID = doc.id
                        //    val likes = doc.data!!["likes"].toString().toInt()
                        //   val dislikes = doc.data!!["dislikes"].toString().toInt()
                        val post = ForumPost(
                            title = title,
                            text = text,
                            userID = userID,
                            timestamp = timestamp,
                            comments = comments,
                            likesIDs = likesIDs,
                            dislikesIDs = dislikesIDs,
                            //likes = likes,
                            //  dislikes = dislikes,
                            reports = reports,
                            reportIDs = reportIDs,
                            postID = postID
                        )
                        adapter.add(ForumPostItem(post, context!!, postID))
                    }
                    if (view != null)
                        forum_rv.adapter = adapter

                }
            }


    }


    @Suppress("UNCHECKED_CAST") // TODO:
    private fun getSaved(listOfSavedPosts: List<String>, docRef: Query) {

        val adapter = GroupAdapter<ViewHolder>()

        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }

            if (context == null) return@addSnapshotListener

            if (snapshot != null && !snapshot.isEmpty) {
                // clear the adapter
                adapter.clear()
                // get all messages and clear the old
                val documents = snapshot.documents
                for (doc in documents) {

                    if (!listOfSavedPosts.contains(doc.id)) continue

                    val title = doc.data!!["title"].toString()
                    val text = doc.data!!["text"].toString()
                    val userID = doc.data!!["userID"].toString()
                    val timestamp = doc.get("timestamp", Date::class.java)!!

                    val comments: ArrayList<Comment>? = if (doc.data!!["comments"] != null)
                        doc.get("comments") as ArrayList<Comment>?
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

                    val reportIDs: ArrayList<String>? =
                        if (doc.data!!["reportIDs"] != null)
                            doc.get("reportIDs")!! as ArrayList<String>
                        else
                            ArrayList()

                    val reports = doc.data!!["reports"].toString().toInt()

                    val postID = doc.id
                    //    val likes = doc.data!!["likes"].toString().toInt()
                    //   val dislikes = doc.data!!["dislikes"].toString().toInt()
                    val post = ForumPost(
                        title = title,
                        text = text,
                        userID = userID,
                        timestamp = timestamp,
                        comments = comments,
                        likesIDs = likesIDs,
                        dislikesIDs = dislikesIDs,
                        reports = reports,
                        reportIDs = reportIDs,
                        postID = postID
                    )
                    adapter.add(SavedPostItem(post, context!!, postID, adapter))
                }
                if (view != null)
                    forum_rv.adapter = adapter

            }
        }


    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val menuID = if (ForumFragmentArgs.fromBundle(arguments!!).seeBookmarks)
            R.menu.saved_posts_menu
        else
            R.menu.forum_fragment_menu

        inflater.inflate(menuID, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.savedPostsBtn)
            goToSavedPosts()
        else if (item.itemId == R.id.closeSavedPosts)
            view!!.recreateFragment(R.id.forumFragment)
        return super.onOptionsItemSelected(item)
    }


    private suspend fun hasSavedPosts(): Boolean {
        val savedPostsDao = SavedPostsRoomDatabase.getDatabase(context!!).SavedPostsDao()
        return savedPostsDao.getSavedPosts().isNotEmpty()
    }


    private fun goToSavedPosts() {
        val navController = view!!.findNavController()
        GlobalScope.launch {
            if (hasSavedPosts()) {
                activity!!.runOnUiThread {
                    navController.navigate(
                        ForumFragmentDirections.forumToSaved().setSeeBookmarks(true),
                        NavOptions.Builder()
                            .setPopUpTo(R.id.forumFragment, true)
                            .build()
                    )
                }
            } else {
                activity!!.runOnUiThread {
                    context!!.showMessage(getString(R.string.no_saved_posts))
                }
            }

            this.coroutineContext.cancel()
        }
    }


}
