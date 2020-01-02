package com.lemonlab.all_in_one

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.lemonlab.all_in_one.extensions.createImageFile
import dev.sasikanth.colorsheet.ColorSheet
import ja.burhanrashid52.photoeditor.OnPhotoEditorListener
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.ViewType
import kotlinx.android.synthetic.main.color_picker.view.*
import kotlinx.android.synthetic.main.fragment_create.*
import kotlinx.android.synthetic.main.input_text.view.*
import java.lang.Exception


/**
 * A simple [Fragment] subclass.
 */
class CreateFragment : Fragment() {

    private val PICK_IMAGE_CODE = 1
    private val IMAGE_CAPTURE_CODE = 2

    private var currentEditorColor = ColorSheet.NO_COLOR
    private var colors:IntArray? = null
    private var photoEditor:PhotoEditor? = null

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

        if (item.itemId == R.id.createLibrary){
            selectImage()
        }

        if(item.itemId == R.id.createSave){
            photoEditor!!.saveAsFile(activity!!.createImageFile().path, object: PhotoEditor.OnSaveListener{
                override fun onSuccess(imagePath: String) {
                    Toast.makeText(context!!, "تم حفظ الصورة بنجاح", Toast.LENGTH_LONG).show()
                }

                override fun onFailure(exception: Exception) {
                    Toast.makeText(context!!, "تعذر حفظ الصورة", Toast.LENGTH_LONG).show()
                }

            })
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE_CODE){
            photoEditorView.source.setImageURI(data!!.data)
        }
        else if(resultCode == Activity.RESULT_OK && requestCode == IMAGE_CAPTURE_CODE){
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
        colors = resources.getIntArray(R.array.colors)
    }

    private fun editor() {

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
                R.id.bruchTool -> {
                    photoEditor!!.setBrushDrawingMode(true)
                    photoEditor!!.brushColor = currentEditorColor
                }

                R.id.eraserTool-> {
                    photoEditor!!.brushEraser()
                }

                R.id.textTool -> {
                    photoEditor!!.addText(
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

        dialog(photoEditor!!) // pass the editor to the edit text dialog

        // undo and redo
        undo_btn.setOnClickListener {
            photoEditor!!.undo()
        }

        redo_btn.setOnClickListener {
            photoEditor!!.redo()
        }
    }

    private fun showColorPicker(){
        ColorSheet().colorPicker(
            colors = colors!!,
            listener = { color ->
                currentEditorColor = color
            })
            .show(fragmentManager!!)
    }

    private fun selectImage(){
        // Request permissions to storage
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (activity!!.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED){
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                requestPermissions(permissions, 1)
            }
            else{
                pickImageFromGallery()
            }
        }
        else{
            //system OS is < Marshmallow
            pickImageFromGallery()
        }
    }


    private fun pickImageFromGallery(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_CODE)
    }

    private fun selectCameraImage(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (activity!!.checkSelfPermission(Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED ||
                activity!!.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED){
                //permission was not enabled
                val permission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                //show popup to request permission
                requestPermissions(permission, 1000)
            }
            else{
                //permission already granted
                openCamera()
            }
        }
        else{
            //system os is < marshmallow
            openCamera()
        }
    }

    private fun openCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(activity!!.packageManager)?.also {
                startActivityForResult(takePictureIntent, IMAGE_CAPTURE_CODE)
            }
        }
    }


}
