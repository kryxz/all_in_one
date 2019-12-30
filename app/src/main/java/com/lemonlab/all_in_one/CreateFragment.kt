package com.lemonlab.all_in_one

import android.os.Bundle
import android.view.*
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import ja.burhanrashid52.photoeditor.PhotoEditor
import kotlinx.android.synthetic.main.fragment_create.*


/**
 * A simple [Fragment] subclass.
 */
class CreateFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_create, container, false)
    }

    // options menu in app bar
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.photo_editor_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.createCamera)
            return true

        if(item.itemId == R.id.createLibrary)
            return true

        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        photoEditorView.source.setImageResource(R.drawable.android_logo)

        //Use custom font using latest support library
        val mTextRobotoTf = ResourcesCompat.getFont(context!!, R.font.aram)


        val mPhotoEditor = PhotoEditor.Builder(context!!, photoEditorView)
            .setPinchTextScalable(true)
            .setDefaultTextTypeface(mTextRobotoTf)
            .setDefaultEmojiTypeface(mTextRobotoTf)
            .build()

        mPhotoEditor.setBrushDrawingMode(true)
        mPhotoEditor.addText(mTextRobotoTf,"Hello", R.color.colorAccent)
    }
}
