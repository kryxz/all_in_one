package com.lemonlab.all_in_one


import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.InterstitialAd
import com.lemonlab.all_in_one.extensions.Ads
import com.lemonlab.all_in_one.extensions.addAd
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
        lateinit var fullScreenAd: InterstitialAd
        fun showAd() {
            fullScreenAd.show()
            Ads.loadFullScreenAd(fullScreenAd)
        }

        lateinit var favoritesViewModel: FavoritesViewModel
        lateinit var favoritesCodes: MutableList<Int>
        var showImage = true
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_local_quotes, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val category = LocalQuotesFragmentArgs.fromBundle(arguments!!).category
        init(category)
        setTitle(category)
        fullScreenAd = InterstitialAd(context!!)
        Ads.loadFullScreenAd(fullScreenAd)
    }


    private fun init(category: Category) {
        val adapter = GroupAdapter<ViewHolder>()
        val list = getStatuses(category, resources)

        favoritesViewModel = ViewModelProviders.of(this)[FavoritesViewModel::class.java]
        val linearLayoutManager = LinearLayoutManager(context)

        quotes_rv.adapter = adapter
        quotes_rv.layoutManager = linearLayoutManager
        favoritesViewModel.updateCodes()

        favoritesCodes = if (favoritesViewModel.favoritesCodes.value != null)
            favoritesViewModel.favoritesCodes.value!!.toMutableList()
        else
            mutableListOf()

        showImage =
            context!!.getSharedPreferences("UserPrefs", 0)
                .getBoolean("showImages", true)

        for ((index, quote) in list.withIndex()) {
            adapter.add(QuoteItem(quote, category))
            // adds an ad every n items
            addAd(index, adapter)
        }



        favoritesViewModel.allFavorites.observe(this, Observer<List<Favorite>> {
            adapter.notifyDataSetChanged()
        })

        favoritesViewModel.favoritesCodes.observe(this, Observer<List<Int>> { codes ->
            favoritesCodes = codes?.toMutableList() ?: mutableListOf()
            adapter.notifyDataSetChanged()
        })


    }

    // Title changes with the category
    private fun setTitle(category: Category) =
        activity!!.setFragmentTitle(getString(category.textID))


    fun getStatuses(category: Category, resources: Resources): List<String> =
        resources.getStringArray(category.arrayID).toList()


}
