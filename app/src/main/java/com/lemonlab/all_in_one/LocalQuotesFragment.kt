package com.lemonlab.all_in_one


import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.lemonlab.all_in_one.extensions.setFragmentTitle
import com.lemonlab.all_in_one.items.Category
import com.lemonlab.all_in_one.items.QuoteItem
import com.lemonlab.all_in_one.model.Favorite
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_local_quotes.*

/**
 * A simple [Fragment] subclass.
 */


class LocalQuotesFragment : Fragment() {

    companion object {
        lateinit var favoritesViewModel: FavoritesViewModel
        lateinit var favoritesCodes: MutableList<Int>
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_local_quotes, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val category = LocalQuotesFragmentArgs.fromBundle(arguments!!).category

        init(category)
        setTitle(category)

        super.onViewCreated(view, savedInstanceState)
    }


    private fun init(category: Category) {
        val adapter = GroupAdapter<ViewHolder>()
        val list = getStatuses(category, resources)

        favoritesViewModel = ViewModelProviders.of(this)[FavoritesViewModel::class.java]
        quotes_rv.adapter = adapter
        favoritesViewModel.updateCodes()

        favoritesCodes = if (favoritesViewModel.favoritesCodes.value != null)
            favoritesViewModel.favoritesCodes.value!!.toMutableList()
        else
            mutableListOf()


        for (quote in list)
            adapter.add(QuoteItem(context!!, quote, category))


        favoritesViewModel.allFavorites.observe(this, Observer<List<Favorite>> { fav ->
            adapter.notifyDataSetChanged()
        })

        favoritesViewModel.favoritesCodes.observe(this, Observer<List<Int>> { codes ->
            favoritesCodes = codes?.toMutableList() ?: mutableListOf()
            adapter.notifyDataSetChanged()
        })


    }

    // Title changes with the category
    private fun setTitle(category: Category) {
        val categoryIndex =
            resources.getStringArray(R.array.categoryEn).indexOf(category.toString())

        activity!!.setFragmentTitle(resources.getStringArray(R.array.category)[categoryIndex])

    }


    fun getStatuses(category: Category, resources: Resources): List<String> {
        return when (category) {
            Category.Wisdom -> resources.getStringArray(R.array.wisdom).toList()
            Category.Friendship -> resources.getStringArray(R.array.friendship).toList()
            Category.Afternoon -> resources.getStringArray(R.array.afternoon).toList()
            Category.Other -> resources.getStringArray(R.array.other).toList()
            Category.Winter -> resources.getStringArray(R.array.winter).toList()
            Category.Love -> resources.getStringArray(R.array.love).toList()
            Category.Morning -> resources.getStringArray(R.array.morning).toList()
            Category.Islam -> resources.getStringArray(R.array.islam).toList()
            Category.Sadness -> resources.getStringArray(R.array.sadness).toList()
        }

    }

}
