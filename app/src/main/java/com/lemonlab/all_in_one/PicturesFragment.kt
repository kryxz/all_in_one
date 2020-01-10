package com.lemonlab.all_in_one


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.lemonlab.all_in_one.items.UserImageItem
import com.lemonlab.all_in_one.items.UserStatusItem
import com.lemonlab.all_in_one.model.UserStatusImage
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_pictures.*

/**
 * A simple [Fragment] subclass.
 */
class PicturesFragment : Fragment() {

    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pictures, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getUsersStatusImage()
    }

    private fun getUsersStatusImage(){
        val db = FirebaseFirestore.getInstance()
        db.collection("users_images").get().addOnSuccessListener {
            if(it != null){
                // set the adapter
                pictures_rv.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                pictures_rv.adapter = adapter

                // add items to the rv adapter
                for(item in it.documents){
                    val url = item["url"].toString()
                    adapter.add(UserImageItem(url))
                }
            }
        }
    }

}
