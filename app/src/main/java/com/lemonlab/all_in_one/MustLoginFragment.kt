package com.lemonlab.all_in_one


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_must_login.*


/*
 // if user not logged in, ask them to register or login.
 */
class MustLoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_must_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        checkUser()
        super.onViewCreated(view, savedInstanceState)
    }


    private fun checkUser() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null)
        // go to login fragment.
            mustLoginBtn.setOnClickListener {
                val navOptions = NavOptions.Builder().setPopUpTo(R.id.mainFragment, true).build()
                view!!.findNavController().navigate(
                    R.id.loginFragment,
                    null, navOptions
                )
            }

    }
}
