package com.lemonlab.all_in_one


import android.app.Application
import android.content.Context
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.lifecycle.Observer
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pictures, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        picturesViewModel = ViewModelProviders.of(this)[PicturesViewModel::class.java]
        initAdapter()
    }

    private fun initAdapter() {
        val adapter = GroupAdapter<ViewHolder>()
        pictures_rv.adapter = adapter
        var oldSize = 0
        picturesViewModel.getImages().observe(this, Observer {
            if (it.size == oldSize) return@Observer
            oldSize = it.size
            adapter.clear()
            for (image in it)
                adapter.add(UserImageItem(image, adapter))
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
            allImages.value = imagesList
        }
        return allImages
    }


    fun saveImage(url: String, context: Context) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val bitmap = Picasso.get().load(url).get()
                val uuid = UUID.randomUUID().toString().subSequence(0, 10)
                MediaStore.Images.Media.insertImage(
                    context.contentResolver,
                    bitmap,
                    context.getString(R.string.app_name) + uuid,
                    context.getString(R.string.app_name)
                )
                context.showMessage(context.getString(R.string.image_saved))
            }
        }

    }


}

