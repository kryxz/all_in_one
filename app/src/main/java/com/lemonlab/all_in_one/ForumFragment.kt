package com.lemonlab.all_in_one


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.lemonlab.all_in_one.extensions.*
import com.lemonlab.all_in_one.items.ForumPostItem
import com.lemonlab.all_in_one.items.SavedPostItem
import com.lemonlab.all_in_one.model.ForumPost
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_forum.*


/*
    Users Forum. Users can start discussions, post comments and more.
 */


class ForumFragment : Fragment() {

    companion object {
        lateinit var lifecycleOwner: LifecycleOwner
        lateinit var postsViewModel: UsersTextsViewModel
        var savedPosts: List<String> = mutableListOf()
    }

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
        init()
        activity!!.hideKeypad(view)

        super.onViewCreated(view, savedInstanceState)
    }


    private fun init() {
        lifecycleOwner = viewLifecycleOwner
        postsViewModel = ViewModelProviders.of(this)[UsersTextsViewModel::class.java]

        create_post_btn.setOnClickListener {
            it.findNavController().navigate(ForumFragmentDirections.createNewPost())
        }
        val adapter = GroupAdapter<ViewHolder>()


        if (postsViewModel.savedPostsIDs.value != null)
            savedPosts = postsViewModel.savedPostsIDs.value!!.toMutableList()

        postsViewModel.savedPostsIDs.observe(this, Observer {
            savedPosts = it!!.toMutableList()
        })

        val seeBookmarks = ForumFragmentArgs.fromBundle(arguments!!).seeBookmarks
        if (seeBookmarks) {
            adapter.clear()
            viewSavedPosts()
            return
        }

        forum_rv.adapter = adapter

        val observer = Observer<List<ForumPost>> {
            adapter.clear()

            // show hint to the user
            if (it.isEmpty()) {
                empty_post_text_view.visibility = View.VISIBLE
            } else {
                empty_post_text_view.visibility = View.GONE
            }

            for (item in it)
                adapter.add(ForumPostItem(item, context!!))
        }

        postsViewModel.getPosts().observe(this, observer)


    }

    private fun viewSavedPosts() {
        val adapter = GroupAdapter<ViewHolder>()

        empty_post_text_view.visibility = View.GONE

        activity!!.setFragmentTitle(getString(R.string.saved_posts))
        val observer = Observer<List<ForumPost>> {
            adapter.clear()
            if (it.isEmpty())
                postsViewModel.removeAllSavedPosts()

            for (item in it)
                adapter.add(SavedPostItem(item, adapter))
        }
        postsViewModel.getSavedPosts().observe(this, observer)
        postsViewModel.savedPostsIDs.observe(this, Observer {
            if (it.isEmpty())
                view!!.recreateFragment(R.id.forumFragment)
        })
        forum_rv.adapter = adapter


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


    private fun goToSavedPosts() {
        val navController = view!!.findNavController()
        if (savedPosts.isNotEmpty())
            navController.navigate(
                ForumFragmentDirections.forumToSaved().setSeeBookmarks(true),
                NavOptions.Builder()
                    .setPopUpTo(R.id.forumFragment, true)
                    .build()
            )
        else
            context!!.showMessage(getString(R.string.no_saved_posts))
    }

}
