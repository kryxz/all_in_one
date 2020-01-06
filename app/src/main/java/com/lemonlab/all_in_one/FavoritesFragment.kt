package com.lemonlab.all_in_one


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.lemonlab.all_in_one.extensions.showMessage
import com.lemonlab.all_in_one.items.FavoriteItem
import com.lemonlab.all_in_one.model.Favorite
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_favorites.*

/**
 * A fragment to hold all favorites.
 */


class FavoritesFragment : Fragment() {

    companion object {
        lateinit var favoritesViewModel: FavoritesViewModel
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
        super.onViewCreated(view, savedInstanceState)
    }


    private fun init() {
        val adapter = GroupAdapter<ViewHolder>()

        favorites_rv.adapter = adapter
        favoritesViewModel = ViewModelProviders.of(this)[FavoritesViewModel::class.java]



        favoritesViewModel.allFavorites.observe(this, Observer<List<Favorite>> { fav ->
            // update UI
            if (fav.isEmpty())
                quitFragment()
            else {
                adapter.clear()
                for (item in fav)
                    adapter.add(FavoriteItem(context!!, item))
            }

        })

    }

    private fun quitFragment() {
        view!!.findNavController().navigateUp()
        context!!.showMessage(getString(R.string.no_favorites))
    }

}
