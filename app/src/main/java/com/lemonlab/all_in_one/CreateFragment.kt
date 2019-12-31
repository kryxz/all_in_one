package com.lemonlab.all_in_one

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.gigamole.navigationtabstrip.NavigationTabStrip
import ja.burhanrashid52.photoeditor.PhotoEditor
import kotlinx.android.synthetic.main.fragment_create.*


/**
 * A simple [Fragment] subclass.
 */
class CreateFragment : Fragment() {

    private lateinit var editorButtons:List<View>

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

        //init

        photoEditorView.source.setImageResource(R.drawable.android_logo)

        //Use custom font using latest support library
        val mTextRobotoTf = ResourcesCompat.getFont(context!!, R.font.aram)


        val mPhotoEditor = PhotoEditor.Builder(context!!, photoEditorView)
            .setPinchTextScalable(true)
            .setDefaultTextTypeface(mTextRobotoTf)
            .setDefaultEmojiTypeface(mTextRobotoTf)
            .build()

        navigationTabStrip.onTabStripSelectedIndexListener = object:
            NavigationTabStrip.OnTabStripSelectedIndexListener{
            override fun onEndTabSelected(title: String?, index: Int) {

            }

            override fun onStartTabSelected(title: String?, index: Int) {
                Log.i("CreateFragment", "clicked: $index")
                when(index){
                    0 -> {
                        mPhotoEditor.setBrushDrawingMode(true)
                    }

                    1 ->{
                        mPhotoEditor.brushEraser()
                    }

                    2 ->{
                        mPhotoEditor.addText("Hello World", ContextCompat.getColor(context!!, R.color.colorAccent))
                    }
                }
            }

        }

        // for test
        undo_btn.setOnClickListener {
            mPhotoEditor.undo()
        }

        redo_btn.setOnClickListener {
            mPhotoEditor.redo()
        }
    }

    // change the color of btn to selected color
    private fun enableButton(btn:View){
        for (button in editorButtons){
            if(editorButtons == btn){
                button.setBackgroundColor(ContextCompat.getColor(context!!, R.color.colorPrimaryDark))
            }else{
                button.setBackgroundColor(ContextCompat.getColor(context!!, R.color.colorAccent))
            }
        }
    }
}
