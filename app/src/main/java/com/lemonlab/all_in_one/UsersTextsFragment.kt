package com.lemonlab.all_in_one


import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.lemonlab.all_in_one.items.UserStatusItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_users_texts.*
import kotlinx.android.synthetic.main.sort_texts_by_dialog.view.*

/**
 * A fragment to view users texts in a recycler view.
 */
class UsersTextsFragment : Fragment() {

    companion object {
        lateinit var lifecycleOwner: LifecycleOwner
        lateinit var statusesViewModel: UsersTextsViewModel
        lateinit var favoritesViewModel: FavoritesViewModel
    }

    private val adapter = GroupAdapter<ViewHolder>()

    private var currentSort = SortBy.Time

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_users_texts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()

        super.onViewCreated(view, savedInstanceState)
    }


    private fun init() {
        statusesViewModel = ViewModelProviders.of(this)[UsersTextsViewModel::class.java]
        favoritesViewModel = ViewModelProviders.of(this)[FavoritesViewModel::class.java]

        lifecycleOwner = viewLifecycleOwner
        getTextsBy(SortBy.Time)
    }

    private fun getTextsBy(sortBy: SortBy) {
        users_texts_rv.adapter = adapter
        var oldCount = 0
        statusesViewModel.getStatuses(sortBy).observe(this, Observer {
            if (it.size == oldCount) return@Observer
            oldCount = it.size
            adapter.clear()
            for (item in it)
                adapter.add(UserStatusItem(item))

        })
        currentSort = sortBy
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.users_texts_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.textsSortBy)
            showSortDialog()
        return super.onOptionsItemSelected(item)
    }

    private fun showSortDialog() {

        val dialogBuilder = AlertDialog.Builder(context!!).create()

        val dialogView = with(LayoutInflater.from(context!!)) {
            inflate(
                R.layout.sort_texts_by_dialog,
                null
            )
        }

        with(dialogView) {
            for (button in listOf(sort_dialog_likes, sort_dialog_time))
                button.setOnClickListener {

                    when (button) {

                        sort_dialog_likes -> {
                            if (currentSort == SortBy.Likes) {
                                dialogBuilder.dismiss()
                                return@setOnClickListener
                            }
                            getTextsBy(SortBy.Likes)
                        }

                        sort_dialog_time -> {
                            if (currentSort == SortBy.Time) {
                                dialogBuilder.dismiss()
                                return@setOnClickListener
                            }
                            getTextsBy(SortBy.Time)
                        }


                    }
                    dialogBuilder.dismiss()
                    adapter.clear()
                }

        }

        with(dialogBuilder) {
            setView(dialogView)
            show()
        }

    }

}
