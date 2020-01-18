package com.lemonlab.all_in_one


import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.lemonlab.all_in_one.extensions.hideKeypad
import com.lemonlab.all_in_one.extensions.showMessage
import com.lemonlab.all_in_one.items.CategoryPics
import com.lemonlab.all_in_one.items.FavoriteItem
import com.lemonlab.all_in_one.model.Favorite
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_favorites.*
import kotlin.random.Random


/**
 * A fragment to hold all favorites.
 */


class FavoritesFragment : Fragment() {

    companion object {
        lateinit var favoritesViewModel: FavoritesViewModel
    }

    private val adapter = GroupAdapter<ViewHolder>()
    private val listOfIndicesPairs = ArrayList<Pair<Int, Int>>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
        super.onViewCreated(view, savedInstanceState)
    }


    private fun init() {

        favorites_rv.adapter = adapter
        favoritesViewModel = ViewModelProviders.of(this)[FavoritesViewModel::class.java]

        favoritesViewModel.allFavorites.observe(this, Observer<List<Favorite>> { fav ->
            // update UI
            if (fav.isEmpty())
                quitFragment()
            else {
                adapter.clear()
                for (item in fav) {
                    val categoryIndex = Random.nextInt(CategoryPics.categoryLimit())
                    val picIndex = Random.nextInt(CategoryPics.picsLimit())
                    val indexPair = Pair(categoryIndex, picIndex)
                    adapter.add(FavoriteItem(item, indexPair))
                    listOfIndicesPairs.add(indexPair)
                }
            }

        })


    }

    private fun search(text: String) {
        adapter.clear()
        for ((index, item) in favoritesViewModel.allFavorites.value!!.withIndex())
            if (item.text.contains(text))
                adapter.add(FavoriteItem(item, listOfIndicesPairs[index]))


    }

    private fun quitFragment() {
        view!!.findNavController().navigateUp()
        context!!.showMessage(getString(R.string.no_favorites))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fav_search, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onPrepareOptionsMenu(menu: Menu) {
        (menu.findItem(R.id.search).actionView as SearchView).setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                activity!!.hideKeypad()
                if (adapter.itemCount == 0)
                    context!!.showMessage(getString(R.string.no_result))
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                search(newText)
                return false
            }
        })
        super.onPrepareOptionsMenu(menu)
    }


}
