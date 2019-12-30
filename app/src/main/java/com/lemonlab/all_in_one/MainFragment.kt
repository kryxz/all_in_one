package com.lemonlab.all_in_one


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lemonlab.all_in_one.extensions.makeTheUserOnline


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
        super.onViewCreated(view, savedInstanceState)

        view.makeTheUserOnline()

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

        if(item.itemId == R.id.signOut)
            signOut()

        return super.onOptionsItemSelected(item)
    }

    private fun signOut(){
        FirebaseAuth.getInstance().signOut()
    }
}
