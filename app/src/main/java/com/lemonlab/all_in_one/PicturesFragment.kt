package com.lemonlab.all_in_one


import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.lemonlab.all_in_one.extensions.showMessage
import com.lemonlab.all_in_one.items.UserImageItem
import com.lemonlab.all_in_one.model.UserStatusImage
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_pictures.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class PicturesFragment : Fragment() {


    companion object {
        lateinit var picturesViewModel: PicturesViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pictures, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        picturesViewModel = ViewModelProviders.of(this)[PicturesViewModel::class.java]
        initAdapter()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.pictures_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.goToSendImage)
            view!!.findNavController().navigate(R.id.sendImageFragment)
        return super.onOptionsItemSelected(item)
    }


    private fun initAdapter() {
        val adapter = GroupAdapter<ViewHolder>()
        pictures_rv.adapter = adapter
        var oldSize = 0
        picturesViewModel.getImages().observe(this, Observer {
            if (it.isEmpty())
                no_images_text_view.visibility = View.VISIBLE
            else
                no_images_text_view.visibility = View.GONE
            if (it.size == oldSize) return@Observer
            oldSize = it.size
            adapter.clear()
            for (image in it)
                adapter.add(UserImageItem(image, adapter, activity!!))
        })
    }

}


class PicturesViewModel(application: Application) : AndroidViewModel(application) {

    private val allImages: MutableLiveData<List<UserStatusImage>> = MutableLiveData()

    private val repository = FireStoreRepository()

    fun getImages(): LiveData<List<UserStatusImage>> {
        val imagesList = mutableListOf<UserStatusImage>()
        repository.getImagesRef().addSnapshotListener { snapshot, e ->
            if (e != null) return@addSnapshotListener
            if (snapshot == null) return@addSnapshotListener
            val documents = snapshot.documents
            imagesList.clear()
            for (item in documents) {
                if (item == null) continue
                imagesList.add(item.toObject(UserStatusImage::class.java)!!)
            }

            imagesList.sortByDescending {
                it.timestamp.time
            }
            allImages.value = imagesList
        }
        return allImages
    }


    fun saveImage(url: String, activity: Activity) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val bitmap = Picasso.get().load(url).get()
                val uuid = UUID.randomUUID().toString().subSequence(0, 10)
                MediaStore.Images.Media.insertImage(
                    activity.contentResolver,
                    bitmap,
                    activity.getString(R.string.app_name) + uuid,
                    activity.getString(R.string.app_name)
                )
                activity.runOnUiThread {
                    activity.showMessage(activity.getString(R.string.image_saved))
                }
            }
        }

    }


}

