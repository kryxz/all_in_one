package com.lemonlab.all_in_one


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.lemonlab.all_in_one.items.UserStatusItem
import com.lemonlab.all_in_one.model.UserStatus
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_users_texts.*

/**
 * A fragment to view users texts in a recycler view.
 */
class UsersTextsFragment : Fragment() {

    companion object {
        lateinit var lifecycleOwner: LifecycleOwner
        lateinit var statusesViewModel: UsersTextsViewModel
        lateinit var favoritesViewModel: FavoritesViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_users_texts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()

        super.onViewCreated(view, savedInstanceState)
    }


    private fun init() {
        statusesViewModel = ViewModelProviders.of(this)[UsersTextsViewModel::class.java]
        favoritesViewModel = ViewModelProviders.of(this)[FavoritesViewModel::class.java]

        val adapter = GroupAdapter<ViewHolder>()

        users_texts_rv.adapter = adapter
        lifecycleOwner = viewLifecycleOwner
        var oldStatuses: List<UserStatus> = listOf()
        statusesViewModel.getStatuses().observe(this, Observer {
            if (it.size == oldStatuses.size) return@Observer
            oldStatuses = it
            adapter.clear()
            for (item in it)
                adapter.add(UserStatusItem(item, context!!))

        })


    }


}
