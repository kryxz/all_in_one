package com.lemonlab.all_in_one


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.lemonlab.all_in_one.extensions.makeTheUserOnline
import com.lemonlab.all_in_one.items.MainFragmentItem
import com.lemonlab.all_in_one.items.MainItem
import com.lemonlab.all_in_one.model.Favorite
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_main.*


class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // View created successfully. Call any methods here.
        init()
        makeTheUserOnline()


        checkName()
        super.onViewCreated(view, savedInstanceState)
    }

    // options menu in app bar
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.settingsFragment)
        // navigate from a fragment to another
        // the direction is created in navigation.xml
            view!!.findNavController().navigate(MainFragmentDirections.mainToSettings())

        return super.onOptionsItemSelected(item)
    }


    private fun init() {
        val adapter = GroupAdapter<ViewHolder>()
        val favoritesViewModel =
            ViewModelProviders.of(this)[FavoritesViewModel::class.java]


        addItems(adapter)
        main_rv.adapter = adapter
        favoritesViewModel.allFavorites.observe(this, Observer<List<Favorite>> { fav ->
            // update UI
            adapter.clear()
            if (fav.isNotEmpty())
                adapter.add(MainFragmentItem(MainItem.Favorites))
            addItems(adapter)
        })

    }

    private fun addItems(adapter: GroupAdapter<ViewHolder>) {
        adapter.add(MainFragmentItem(MainItem.Quotes))
        adapter.add(MainFragmentItem(MainItem.UsersTexts))
        adapter.add(MainFragmentItem(MainItem.Pictures))

    }


    // will remove later...
    private fun checkName() {

        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) return
        val userID = auth.uid!!
        FirebaseFirestore.getInstance().collection("users").document(userID).get()
            .addOnSuccessListener {
                if (it == null) return@addOnSuccessListener
                auth.currentUser!!.updateProfile(
                    UserProfileChangeRequest.Builder().setDisplayName(
                        it.data!!["name"].toString()
                    ).build()
                )
            }
    }


}
