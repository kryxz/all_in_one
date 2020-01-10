package com.lemonlab.all_in_one


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lemonlab.all_in_one.items.Option
import com.lemonlab.all_in_one.items.SettingsItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_settings.*

/*
    Shows several options the user can change.
 */

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun init() {
        val adapter = GroupAdapter<ViewHolder>()
        settings_rv.adapter = adapter
        val options = listOf(
            Option.ClearCache,
            Option.DarkTheme,
            Option.SignOut,
            Option.PrivacyPolicy,
            Option.FAQ,
            Option.MoreApps
        )


        for (item in options)
            adapter.add(SettingsItem(item, activity!!))


    }


}
