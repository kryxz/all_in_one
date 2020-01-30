package com.lemonlab.all_in_one


import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.lemonlab.all_in_one.extensions.getBitmapFromView
import com.lemonlab.all_in_one.extensions.showMessage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_view_image.*
import java.util.*


/**
 * A full screen fragment to view image.
 */
class ViewImageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        init()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun init() {
        val activity = activity!!
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        (activity.findViewById(R.id.mainActivity) as View)
            .setBackgroundColor(getColor(R.color.black))

        val url = ViewImageFragmentArgs.fromBundle(arguments!!).url

        CircularProgressDrawable(context!!).apply {
            strokeWidth = 12f
            centerRadius = 120f
            start()
            setColorSchemeColors(ContextCompat.getColor(context!!, R.color.white))
            Picasso.get().load(url).placeholder(this).into(view_ImageView)
        }

        view_image_save.setOnClickListener {
            saveImage()
        }
    }

    private fun saveImage() {

        val uuid = UUID.randomUUID().toString().subSequence(0, 10)
        MediaStore.Images.Media.insertImage(
            context!!.contentResolver,
            view_ImageView.getBitmapFromView(),
            getString(R.string.app_name) + uuid,
            getString(R.string.app_name)
        )
        context!!.showMessage(getString(R.string.image_saved))
    }

    override fun onDestroyView() {
        activity!!.window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        activity!!.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        revertBackgroundColor()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        super.onDestroyView()
    }

    private fun revertBackgroundColor() {
        val backgroundView = activity!!.findViewById(R.id.mainActivity) as View
        val sharedPrefs = context!!.getSharedPreferences("UserPrefs", 0)
        val isDarkTheme = sharedPrefs.getBoolean("isDarkTheme", false)
        with(backgroundView) {
            if (isDarkTheme) setBackgroundColor(getColor(R.color.darkBackgroundColor))
            else setBackgroundColor(getColor(R.color.white))
        }

    }

    private fun getColor(id: Int) = ContextCompat.getColor(context!!, id)
}
