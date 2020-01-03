package com.lemonlab.all_in_one

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.transition.Visibility
import com.lemonlab.all_in_one.extensions.createImageFile
import dev.sasikanth.colorsheet.ColorSheet
import ja.burhanrashid52.photoeditor.OnPhotoEditorListener
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.ViewType
import kotlinx.android.synthetic.main.fragment_create.*
import kotlinx.android.synthetic.main.input_text.view.*


/**
 * A simple [Fragment] subclass.
 */
class CreateFragment : Fragment() {

    private val MAX_BRUSH_SIZE = 128f
    private val MIN_BRUSH_SIZE = 12f

    private lateinit var photoEditor: PhotoEditor
    private var currentEditorBackground:Int = R.drawable.editor_image0

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
            selectCameraImage()

        if (item.itemId == R.id.createLibrary) {
            selectImage()
        }

        if (item.itemId == R.id.changeBackground){
            changeEditorImage()
        }

        if (item.itemId == R.id.createSave) {
            photoEditor.saveAsFile(
                activity!!.createImageFile().path,
                object : PhotoEditor.OnSaveListener {
                    override fun onSuccess(imagePath: String) {
                        Toast.makeText(
                            context!!,
                            getString(R.string.image_saved),
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    override fun onFailure(exception: Exception) {
                        Toast.makeText(
                            context!!,
                            getString(R.string.couldnt_save_image),
                            Toast.LENGTH_LONG
                        ).show()
                    }

                })
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            photoEditorView.source.setImageURI(data!!.data)
        } else if (resultCode == Activity.RESULT_OK && requestCode == 2) {
            val imageBitmap = data!!.extras!!.get("data") as Bitmap
            photoEditorView.source.setImageBitmap(imageBitmap)
        }
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
                x = 400 // x position
                y = 0 // y position
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

        var currentEditorColor = ColorSheet.NO_COLOR

        fun showColorPicker() {
            ColorSheet().colorPicker(
                colors = resources.getIntArray(R.array.colors),
                listener = { color ->
                    currentEditorColor = color
                    // change brush color
                    photoEditor.brushColor = currentEditorColor
                })
                .show(fragmentManager!!)
        }

        //init

        photoEditorView.source.setImageResource(R.drawable.test_image)

        //Use custom font using latest support library
        val mTextRobotoTf = ResourcesCompat.getFont(context!!, R.font.aram)


        photoEditor = PhotoEditor.Builder(context!!, photoEditorView)
            .setPinchTextScalable(true)
            .setDefaultTextTypeface(mTextRobotoTf)
            .setDefaultEmojiTypeface(mTextRobotoTf)
            .build()

        photoEditorBottomBar.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.brushTool -> {
                    photoEditor.setBrushDrawingMode(true)
                    photoEditor.brushColor = currentEditorColor

                    // show buttons related to brush tool
                    increment_brush_size_btn.visibility = View.VISIBLE
                    decrement_brush_size_btn.visibility = View.VISIBLE

                    // start the listeners
                    incrementBrushSize()
                    decrementBrushSize()
                }

                R.id.eraserTool -> {
                    photoEditor.brushEraser()

                    // hide other tools
                    hidBrushTools()
                }

                R.id.textTool -> {
                    photoEditor.addText(
                        "Hello!",
                        currentEditorColor
                    )

                    // hide other tools
                    hidBrushTools()
                }

                // for test
                R.id.emojiTool -> {
                    showColorPicker()

                    // hide other tools
                    hidBrushTools()
                }
            }
            true
        }

        dialog(photoEditor) // pass the editor to the edit text dialog

        // undo and redo
        undo_btn.setOnClickListener {
            photoEditor.undo()
        }

        redo_btn.setOnClickListener {
            photoEditor.redo()
        }
    }


    private fun selectImage() {
        // Request permissions to storage
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity!!.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED
            ) {
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                requestPermissions(permissions, 1)
            } else {
                pickImageFromGallery()
            }
        } else {
            //system OS is < Marshmallow
            pickImageFromGallery()
        }
    }


    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 1)
    }

    private fun selectCameraImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity!!.checkSelfPermission(Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED ||
                activity!!.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED
            ) {
                //permission was not enabled
                val permission =
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                //show popup to request permission
                requestPermissions(permission, 1000)
            } else {
                //permission already granted
                openCamera()
            }
        } else {
            //system os is < marshmallow
            openCamera()
        }
    }

    private fun openCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(activity!!.packageManager)?.also {
                startActivityForResult(takePictureIntent, 2)
            }
        }
    }

    private fun changeEditorImage(){

        // get images from drawables
        val images = listOf(R.drawable.editor_image0,
            R.drawable.editor_image1,
            R.drawable.editor_image2,
            R.drawable.editor_image3,
            R.drawable.editor_image4,
            R.drawable.editor_image5,
            R.drawable.editor_image6,
            R.drawable.editor_image7,
            R.drawable.editor_image8,
            R.drawable.editor_image9,
            R.drawable.editor_image10,
            R.drawable.editor_image11,
            R.drawable.editor_image12,
            R.drawable.editor_image13,
            R.drawable.editor_image14,
            R.drawable.editor_image15)

        // get the current index
        var index = images.indexOf(currentEditorBackground)

        // go to next image
        index = (index + 1) % images.size
        currentEditorBackground = images[index]

        // change editor background
        photoEditorView.source.setImageResource(images[index])
    }

    private fun incrementBrushSize(){
        increment_brush_size_btn.setOnClickListener {
            if(photoEditor.brushSize + 4f <= MAX_BRUSH_SIZE)
                photoEditor.brushSize += 4f
        }
    }

    private fun decrementBrushSize(){
        decrement_brush_size_btn.setOnClickListener {
            if(photoEditor.brushSize - 4f >= MIN_BRUSH_SIZE)
                photoEditor.brushSize -= 4f
        }
    }

    private fun hidBrushTools(){
        increment_brush_size_btn.visibility = View.INVISIBLE
        decrement_brush_size_btn.visibility = View.INVISIBLE
    }

}
