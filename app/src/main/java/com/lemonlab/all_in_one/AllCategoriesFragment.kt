package com.lemonlab.all_in_one


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lemonlab.all_in_one.extensions.addAd
import com.lemonlab.all_in_one.items.CategoryItem
import com.lemonlab.all_in_one.items.categories
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_all_categories.*

/**
 * A fragment to view and browse categories
 */
class AllCategoriesFragment : Fragment() {

    companion object {
        var showImage = true
    }

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

        // adds item to recyclerView adapter
        with(GroupAdapter<ViewHolder>()) {

            // checks if should show image or text only.
            showImage =
                context!!.getSharedPreferences("UserPrefs", 0)
                    .getBoolean("showImages", true)

            category_rv.adapter = this

            for (item in categories)
                add(CategoryItem(item))
            addAd(0, adapter = this)

        }

    }

}
