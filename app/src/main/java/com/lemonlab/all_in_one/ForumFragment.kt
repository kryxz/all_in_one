package com.lemonlab.all_in_one


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.lemonlab.all_in_one.extensions.checkUser
import kotlinx.android.synthetic.main.fragment_forum.*


/*
    Users Forum. Users can start discussions, post comments and more.
 */


class ForumFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forum, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.checkUser()
        createPost()
        super.onViewCreated(view, savedInstanceState)
    }


    private fun createPost() {
        create_post_btn.setOnClickListener {
            it.findNavController().navigate(ForumFragmentDirections.createNewPost())
        }

    }
}
