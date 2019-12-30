package com.lemonlab.all_in_one


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.lemonlab.all_in_one.extensions.makeTheUserOnline
import com.lemonlab.all_in_one.items.Category
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


    private fun makeItem(image1: Int, image2: Int, text: String, category: Category): CategoryItem {
        return CategoryItem(
            context!!,
            listOf(image1, image2)[Random.nextInt(2)],
            text, category
        )
    }

    private fun init() {
        val adapter = GroupAdapter<ViewHolder>()
        adapter.add(
            makeItem(
                R.drawable.hourglass,
                R.drawable.coffee_book,
                getString(R.string.wisdom),
                Category.Wisdom
            )
        )
        adapter.add(
            makeItem(
                R.drawable.friend_guys,
                R.drawable.friend_girls,
                getString(R.string.friendship),
                Category.Friendship
            )
        )
        adapter.add(
            makeItem(
                R.drawable.guy_sad,
                R.drawable.girl_sad,
                getString(R.string.sadness),
                Category.Sadness
            )
        )
        adapter.add(
            makeItem(
                R.drawable.man_islam,
                R.drawable.mosque_islam,
                getString(R.string.islam),
                Category.Islam
            )
        )
        adapter.add(
            makeItem(
                R.drawable.hand_other,
                R.drawable.flower_other,
                getString(R.string.other),
                Category.Other
            )
        )
        adapter.add(
            makeItem(
                R.drawable.city_morning,
                R.drawable.field_morning,
                getString(R.string.morning),
                Category.Morning
            )
        )
        adapter.add(
            makeItem(
                R.drawable.sign_afternoon,
                R.drawable.man_afternoon,
                getString(R.string.afternoon),
                Category.Afternoon
            )
        )
        adapter.add(
            makeItem(
                R.drawable.bicycle_love,
                R.drawable.couple_love,
                getString(R.string.love),
                Category.Love
            )
        )
        adapter.add(
            makeItem(
                R.drawable.winter_girl,
                R.drawable.winter_snow,
                getString(R.string.winter),
                Category.Winter
            )
        )
        category_rv.adapter = adapter
    }
}
