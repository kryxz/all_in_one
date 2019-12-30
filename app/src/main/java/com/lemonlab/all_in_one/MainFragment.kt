package com.lemonlab.all_in_one


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.lemonlab.all_in_one.extensions.makeTheUserOnline
import com.lemonlab.all_in_one.items.CategoryItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_main.*
import kotlin.random.Random


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


    private fun makeItem(image1: Int, image2: Int, text: String): CategoryItem {
        return CategoryItem(
            context!!,
            listOf(image1, image2)[Random.nextInt(2)],
            text
        )
    }

    private fun init() {
        val adapter = GroupAdapter<ViewHolder>()
        adapter.add(
            makeItem(
                R.drawable.hourglass,
                R.drawable.coffee_book,
                getString(R.string.wisdom)
            )
        )
        adapter.add(
            makeItem(
                R.drawable.friend_guys,
                R.drawable.friend_girls,
                getString(R.string.friendship)
            )
        )
        adapter.add(
            makeItem(
                R.drawable.guy_sad,
                R.drawable.girl_sad,
                getString(R.string.sadness)
            )
        )
        category_rv.adapter = adapter
    }
}
