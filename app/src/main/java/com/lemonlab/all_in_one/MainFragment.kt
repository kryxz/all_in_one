package com.lemonlab.all_in_one


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.lemonlab.all_in_one.extensions.makeTheUserOnline
import com.lemonlab.all_in_one.items.Category
import com.lemonlab.all_in_one.items.CategoryItem
import com.lemonlab.all_in_one.items.FavItem
import com.lemonlab.all_in_one.items.Favorites
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_main.*


class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // View created successfully. Call any methods here.
        init()
        makeTheUserOnline()
        super.onViewCreated(view, savedInstanceState)
    }

    // options menu in app bar
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.settingsFragment)
        // navigate from a fragment to another
        // the direction is created in navigation.xml
            view!!.findNavController().navigate(MainFragmentDirections.mainToSettings())

        if (item.itemId == R.id.signOut)
            signOut()

        return super.onOptionsItemSelected(item)
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }


    private fun makeItem(text: String, category: Category): CategoryItem {
        return CategoryItem(
            context!!,
            text, category
        )
    }

    private fun init() {
        val adapter = GroupAdapter<ViewHolder>()
        if (Favorites.favorites.isNotEmpty())
            adapter.add(FavItem(context!!))

        adapter.add(
            makeItem(
                getString(R.string.wisdom),
                Category.Wisdom
            )
        )
        adapter.add(
            makeItem(
                getString(R.string.friendship),
                Category.Friendship
            )
        )
        adapter.add(
            makeItem(
                getString(R.string.sadness),
                Category.Sadness
            )
        )
        adapter.add(
            makeItem(
                getString(R.string.islam),
                Category.Islam
            )
        )
        adapter.add(
            makeItem(
                getString(R.string.other),
                Category.Other
            )
        )
        adapter.add(
            makeItem(
                getString(R.string.morning),
                Category.Morning
            )
        )
        adapter.add(
            makeItem(
                getString(R.string.afternoon),
                Category.Afternoon
            )
        )
        adapter.add(
            makeItem(
                getString(R.string.love),
                Category.Love
            )
        )
        adapter.add(
            makeItem(
                getString(R.string.winter),
                Category.Winter
            )
        )
        category_rv.adapter = adapter
    }
}
