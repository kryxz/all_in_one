package com.lemonlab.all_in_one


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lemonlab.all_in_one.items.CategoryItem
import com.lemonlab.all_in_one.items.categories
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_all_categories.*

/**
 * A fragment to view and browse categories
 */
class AllCategoriesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_categories, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()

    }


    private fun init() {
        val adapter = GroupAdapter<ViewHolder>()
        category_rv.adapter = adapter
        val sharedPrefs = context!!.getSharedPreferences("UserPrefs", 0)

        val showImage =
            sharedPrefs.getBoolean("showImages", true)

        for (item in categories)
            adapter.add(CategoryItem(item, showImage))

    }

}
