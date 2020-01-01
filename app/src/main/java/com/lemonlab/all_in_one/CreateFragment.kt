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
import kotlinx.android.synthetic.main.color_picker.*
import kotlinx.android.synthetic.main.color_picker.view.*
import kotlinx.android.synthetic.main.fragment_create.*
import kotlinx.android.synthetic.main.input_text.view.*


/**
 * A simple [Fragment] subclass.
 */
class CreateFragment : Fragment() {

    var currentEditorColor = R.color.colorAccent

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
                gravity = Gravity.CENTER or Gravity.START
                x = 400 //x position
                y = 0 //y position
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
                dialogView.inputTextField.setText(text)
                dialogBuilder.setOnDismissListener {
                    val newText = inputText.text.toString()
                    dialogView.inputTextField.text!!.clear()
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

        photoEditorView.source.setImageResource(R.drawable.test_image)

        //Use custom font using latest support library
        val mTextRobotoTf = ResourcesCompat.getFont(context!!, R.font.aram)


        val photoEditor = PhotoEditor.Builder(context!!, photoEditorView)
            .setPinchTextScalable(true)
            .setDefaultTextTypeface(mTextRobotoTf)
            .setDefaultEmojiTypeface(mTextRobotoTf)
            .build()

        photoEditorBottomBar.setOnNavigationItemSelectedListener {
            Log.i("CreateFragment", "item id: $it.itemId")
            when (it.itemId) {
                R.id.bruchTool -> {
                    photoEditor.setBrushDrawingMode(true)
                }

                R.id.eraserTool-> {
                    photoEditor.brushEraser()
                }

                R.id.textTool -> {
                    photoEditor.addText(
                        "Hello!",
                        currentEditorColor
                    )
                }

                // for test
                R.id.emojiTool ->{
                    showColorPicker()
                }
            }
            true
        }
        dialog(photoEditor)
        // for test
        undo_btn.setOnClickListener {
            photoEditor.undo()
        }

        redo_btn.setOnClickListener {
            photoEditor.redo()
        }
    }

    private fun showColorPicker(){
        val dialogBuilder = AlertDialog.Builder(context!!).create()

        val dialogView = layoutInflater.inflate(
            R.layout.color_picker,
            view!!.findViewById(R.id.settingsFragment)
        )

        dialogBuilder.setView(dialogView)
        dialogBuilder.show()

        dialogBuilder.setOnDismissListener {
            currentEditorColor = dialogView.color_picker.color
        }
    }
}
