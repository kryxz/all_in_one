package com.lemonlab.all_in_one


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lemonlab.all_in_one.extensions.showMessage

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
        context!!.showMessage(category.toString())
        super.onViewCreated(view, savedInstanceState)
    }

}
