package com.lemonlab.all_in_one


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lemonlab.all_in_one.extensions.setFragmentTitle
import com.lemonlab.all_in_one.items.Category
import com.lemonlab.all_in_one.items.Favorites
import com.lemonlab.all_in_one.items.QuoteItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_local_quotes.*

/**
 * A simple [Fragment] subclass.
 */


class LocalQuotesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_local_quotes, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val category = LocalQuotesFragmentArgs.fromBundle(arguments!!).category
        initAdapter(category)
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initAdapter(category: Category) {
        // set title
        val categoryIndex =
            resources.getStringArray(R.array.categoryEn).indexOf(category.toString())
        activity!!.setFragmentTitle(resources.getStringArray(R.array.category)[categoryIndex])


        val adapter = GroupAdapter<ViewHolder>()
        val list = getStatuses(category)
        for ((index, quote) in list.withIndex())
            adapter.add(QuoteItem(context!!, quote, category, index))

        Favorites().getCategoryFavorites(context!!, category)
        quotes_rv.adapter = adapter


    }


    private fun getStatuses(category: Category): List<String> {
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
