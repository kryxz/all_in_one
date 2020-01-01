package com.lemonlab.all_in_one

import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.gigamole.navigationtabstrip.NavigationTabStrip
import ja.burhanrashid52.photoeditor.OnPhotoEditorListener
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.ViewType
import kotlinx.android.synthetic.main.fragment_create.*


/**
 * A simple [Fragment] subclass.
 */
class CreateFragment : Fragment() {

    private lateinit var editorButtons: List<View>

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

        if (item.itemId == R.id.createLibrary)
            return true

        return super.onOptionsItemSelected(item)
    }

    private fun dialog(photoEditor: PhotoEditor) {

        val dialogBuilder = AlertDialog.Builder(context!!).create()
        val dialogView = layoutInflater.inflate(
            R.layout.input_text,
            view!!.findViewById(R.id.settingsFragment)
        )

        // the edit text
        val inputText = dialogView.findViewById(R.id.inputTextField) as AppCompatEditText


        dialogBuilder.setView(dialogView)


        // makes dialog transparent
        dialogBuilder.window!!.setBackgroundDrawable(ColorDrawable(0))

        // cancels focus(clears view dim)
        dialogBuilder.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        // shows dialog at location specified by x, y
        with(dialogBuilder) {
            val attrs = window!!.attributes
            with(attrs) {
                gravity = Gravity.TOP or Gravity.START
                x = 400 //x position
                y = 250 //y position
            }
        }

        photoEditor.setOnPhotoEditorListener(object : OnPhotoEditorListener {
            override fun onEditTextChangeListener(
                rootView: View?,
                text: String?,
                colorCode: Int
            ) {

                photoEditor.editText(rootView!!, text, colorCode)
                dialogBuilder.show()
                dialogBuilder.setOnDismissListener {
                    val newText = inputText.text.toString()
                    photoEditor.editText(rootView, newText, colorCode)
                }

            }

            override fun onAddViewListener(viewType: ViewType?, numberOfAddedViews: Int) {
            }

            override fun onRemoveViewListener(viewType: ViewType?, numberOfAddedViews: Int) {
            }

            override fun onStartViewChangeListener(viewType: ViewType?) {
            }

            override fun onStopViewChangeListener(viewType: ViewType?) {
            }
        })

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        editor()
        super.onViewCreated(view, savedInstanceState)

    }

    private fun editor() {

        //init

        photoEditorView.source.setImageResource(R.drawable.android_logo)

        //Use custom font using latest support library
        val mTextRobotoTf = ResourcesCompat.getFont(context!!, R.font.aram)


        val mPhotoEditor = PhotoEditor.Builder(context!!, photoEditorView)
            .setPinchTextScalable(true)
            .setDefaultTextTypeface(mTextRobotoTf)
            .setDefaultEmojiTypeface(mTextRobotoTf)
            .build()

        navigationTabStrip.onTabStripSelectedIndexListener = object :
            NavigationTabStrip.OnTabStripSelectedIndexListener {

            override fun onEndTabSelected(title: String?, index: Int) {

            }

            override fun onStartTabSelected(title: String?, index: Int) {
                Log.i("CreateFragment", "clicked: $index")
                when (index) {
                    0 -> {
                        mPhotoEditor.setBrushDrawingMode(true)
                    }

                    1 -> {
                        mPhotoEditor.brushEraser()
                    }

                    2 -> {
                        mPhotoEditor.addText(
                            "Hello!",
                            ContextCompat.getColor(context!!, R.color.colorAccent)
                        )
                    }
                }
            }

        }
        dialog(mPhotoEditor)
        // for test
        undo_btn.setOnClickListener {
            mPhotoEditor.undo()
        }

        redo_btn.setOnClickListener {
            mPhotoEditor.redo()
        }
    }

    // change the color of btn to selected color
    private fun enableButton(btn: View) {
        for (button in editorButtons) {
            if (editorButtons == btn) {
                button.setBackgroundColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.colorPrimaryDark
                    )
                )
            } else {
                button.setBackgroundColor(ContextCompat.getColor(context!!, R.color.colorAccent))
            }
        }
    }
}
