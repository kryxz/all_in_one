package com.lemonlab.all_in_one


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lemonlab.all_in_one.items.FavoriteItem
import com.lemonlab.all_in_one.items.Favorites
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_favorites.*

/**
 * A fragment to hold all favorites.
 */


class FavoritesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initAdapter()
        super.onViewCreated(view, savedInstanceState)
    }


    private fun initAdapter() {
        val adapter = GroupAdapter<ViewHolder>()
        val favorites = Favorites.favorites
        for (item in favorites)
            adapter.add(FavoriteItem(context!!, item, adapter))

        favorites_rv.adapter = adapter
    }

}
