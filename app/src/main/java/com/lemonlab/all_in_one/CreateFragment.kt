package com.lemonlab.all_in_one


import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.lemonlab.all_in_one.extensions.createImageFile
import com.lemonlab.all_in_one.extensions.removeWhitespace
import com.lemonlab.all_in_one.items.FilterItem
import com.lemonlab.all_in_one.items.FontItem
import com.lemonlab.all_in_one.items.StickerItem
import com.lemonlab.all_in_one.model.UserStatusImage
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import dev.sasikanth.colorsheet.ColorSheet
import ja.burhanrashid52.photoeditor.OnPhotoEditorListener
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.PhotoFilter
import ja.burhanrashid52.photoeditor.ViewType
import kotlinx.android.synthetic.main.fillters_selector_view.view.*
import kotlinx.android.synthetic.main.fonts_selector_view.view.*
import kotlinx.android.synthetic.main.fragment_create.*
import kotlinx.android.synthetic.main.fragment_send_image.*
import kotlinx.android.synthetic.main.input_text.view.*
import kotlinx.android.synthetic.main.stickers_view.view.*
import java.io.ByteArrayOutputStream
import java.sql.Timestamp
import java.util.*
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 */

class CreateFragment : Fragment() {

    private val maxBrushSize = 512f
    private val minBrushSize = 8f

    private lateinit var photoEditor: PhotoEditor
    private var currentEditorBackground: Int = R.drawable.editor_image0
    private var currentFontTypeFace: Typeface? = null // TODO:: Change the first color
    private var imageUri: Uri? = null

    enum class PhotoTool {
        Brush, Text, Eraser, EmojiPicker, ColorPicker
    }

    private var currentSelectedTool: PhotoTool = PhotoTool.Brush

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

        if (item.itemId == R.id.createChangeBackground) {
            changeEditorImage()
        }

        if (item.itemId == R.id.createSave) {
            // save image in the gallery
            saveEditorImage()
        }

        if (item.itemId == R.id.createSend) {
            // save then send the image to firestore
            sendImageStatus()
        }

        return super.onOptionsItemSelected(item)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        editor()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun saveEditorImage() {
        photoEditor.saveAsFile(
            activity!!.createImageFile().path, // get image file path
            object : PhotoEditor.OnSaveListener {
                override fun onSuccess(imagePath: String) {
                    // show message to user
                    Toast.makeText(
                        context!!,
                        getString(R.string.image_saved),
                        Toast.LENGTH_LONG
                    ).show()

                    imageUri = Uri.parse(imagePath)
                }

                override fun onFailure(exception: Exception) {
                    // show message to user
                    Toast.makeText(
                        context!!,
                        getString(R.string.couldnt_save_image),
                        Toast.LENGTH_LONG
                    ).show()
                }

            })
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
                dialogView.inputTextField.setText(text)
                dialogBuilder.show()
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


    private fun showEditTextDialog() {
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

        dialogBuilder.setOnDismissListener {
            if (inputText.text.toString().removeWhitespace().isNotEmpty())
                photoEditor.addText(inputText.text.toString(), ColorSheet.NO_COLOR)
        }

        dialogBuilder.show()
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

        // the used font when user add text for first time
        currentFontTypeFace = ResourcesCompat.getFont(context!!, R.font.amiri_regular)

        // the used background when user open the editor
        photoEditorView.source.setImageResource(R.drawable.test_image)

        //Use custom font using latest support library
        val mTextRobotoTf = ResourcesCompat.getFont(context!!, R.font.aram)


        // create the photo editor using library
        photoEditor = PhotoEditor.Builder(context!!, photoEditorView)
            .setPinchTextScalable(true)
            .setDefaultTextTypeface(mTextRobotoTf)
            .setDefaultEmojiTypeface(mTextRobotoTf)
            .build()

        // set the default selected item
        photoEditorBottomBar.selectedItemId = R.id.brushTool
        photoEditor.setBrushDrawingMode(true)
        photoEditor.brushColor = currentEditorColor

        // listen to editor nav bar items
        photoEditorBottomBar.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                // change current selected photo tool

                R.id.brushTool -> {
                    photoEditor.setBrushDrawingMode(true)
                    photoEditor.brushColor = currentEditorColor

                    // show buttons related to brush tool
                    increment_brush_size_btn.visibility = View.VISIBLE
                    decrement_brush_size_btn.visibility = View.VISIBLE

                    // start the listeners
                    incrementBrushSize()
                    decrementBrushSize()

                    currentSelectedTool = PhotoTool.Brush
                }

                R.id.eraserTool -> {
                    photoEditor.brushEraser()

                    // hide other tools
                    hideBrushTools()

                    //
                    currentSelectedTool = PhotoTool.Eraser
                }

                R.id.textTool -> {
                    showEditTextDialog()
                    // hide other tools
                    hideBrushTools()

                    //
                    currentSelectedTool = PhotoTool.Text
                }

                R.id.colorPickerTool -> {
                    // hide other tools
                    hideBrushTools()

                    showColorPicker()

                    //
                    currentSelectedTool = PhotoTool.ColorPicker
                }

                // for test
                R.id.emojiTool -> {
                    showEmojiDialog()
                    // hide other tools
                    hideBrushTools()

                    //
                    currentSelectedTool = PhotoTool.EmojiPicker
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

        // show fonts dialog
        fonts_btn.setOnClickListener {
            showFontsDialog()
        }

        // show filters selector dialog
        filter_btn.setOnClickListener {
            showPhotoFilterDialog()
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

    private fun changeEditorImage() {

        // get images from drawables
        val images = listOf(
            R.drawable.editor_image0,
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
            R.drawable.editor_image15
        )

        // get the current index
        var index = images.indexOf(currentEditorBackground)

        // go to next image
        index = (index + 1) % images.size
        currentEditorBackground = images[index]

        // change editor background
        photoEditorView.source.setImageResource(images[index])
    }

    private fun incrementBrushSize() {
        increment_brush_size_btn.setOnClickListener {
            if (photoEditor.brushSize + 8f <= maxBrushSize)
                photoEditor.brushSize += 8f
        }
    }

    private fun decrementBrushSize() {
        decrement_brush_size_btn.setOnClickListener {
            if (photoEditor.brushSize - 8f >= minBrushSize)
                photoEditor.brushSize -= 8f
        }
    }

    private fun hideBrushTools() {
        increment_brush_size_btn.visibility = View.INVISIBLE
        decrement_brush_size_btn.visibility = View.INVISIBLE
    }

    private fun showEmojiDialog() {

        // get the view
        val stickerDialogView = layoutInflater.inflate(
            R.layout.stickers_view,
            view!!.findViewById(R.id.createFragment)
        )

        val stickerRv = stickerDialogView.findViewById(R.id.sticker_rv) as RecyclerView


        // create and show the dialog
        val dialogBuilder = AlertDialog.Builder(context!!).create()
        dialogBuilder.setView(stickerDialogView)

        // close the dialog
        stickerDialogView.emoji_selector_view_btn.setOnClickListener {
            dialogBuilder.dismiss()
        }

        fun updateTheAdapter(spanCount: Int) {

            // set the span cont to rv
            stickerRv.layoutManager = GridLayoutManager(context!!, spanCount)

            // stickers adapter
            val adapter = GroupAdapter<ViewHolder>()

            val emojiCodes = PhotoEditor.getEmojis(context!!)
            for (code in emojiCodes) {
                adapter.add(
                    StickerItem(
                        code, ::getDataFromStickerItem,
                        dialog = dialogBuilder, spanCount = spanCount
                    )
                )
            }

            stickerRv.adapter = adapter
        }

        // listen to seek bar to change the span size for sticker rv
        stickerDialogView.seek_sticker.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (progress >= 3) {
                    updateTheAdapter(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                //
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                //
            }

        })

        // get emoji and add it to the adapter then show the dialog
        updateTheAdapter(3)
        dialogBuilder.show()
    }

    // function to get data from Sticker Item when user click on it
    private fun getDataFromStickerItem(textView: TextView) {
        photoEditor.addEmoji(textView.text.toString())
    }

    private fun showFontsDialog() {
        // get the view
        val fontsDialogView = layoutInflater.inflate(
            R.layout.fonts_selector_view,
            view!!.findViewById(R.id.createFragment)
        )

        // get the rv
        val fontsRv = fontsDialogView.fonts_rv

        // set the adapter
        val adapter = GroupAdapter<ViewHolder>()

        // get the fonts from assets
        val fontsTypeFaces = listOf(
            ResourcesCompat.getFont(context!!, R.font.amiri_regular),
            ResourcesCompat.getFont(context!!, R.font.aram),
            ResourcesCompat.getFont(context!!, R.font.baloobhaijaan_regular),
            ResourcesCompat.getFont(context!!, R.font.cairo_regular),
            ResourcesCompat.getFont(context!!, R.font.lalezar_regular),
            ResourcesCompat.getFont(context!!, R.font.rakkas_regular),
            ResourcesCompat.getFont(context!!, R.font.tajawal_regular),
            ResourcesCompat.getFont(context!!, R.font.vibes_regular)
        )

        // create the view and show it
        val dialog = AlertDialog.Builder(context!!).create()
        dialog.setView(fontsDialogView)

        // exit the dialog
        fontsDialogView.fonts_selector_view_btn.setOnClickListener {
            dialog.dismiss()
        }

        // add all fonts to the adapter
        for (font in fontsTypeFaces) {
            adapter.add(
                FontItem(
                    fontFace = font!!, action = ::getDataFromFontsDialog,
                    dialog = dialog
                )
            )
        }


        dialog.show()

        // set the adapter
        fontsRv.adapter = adapter
    }

    // function to get data from Font Item when user click on it
    private fun getDataFromFontsDialog(typeface: Typeface) {
        currentFontTypeFace = typeface
    }

    private fun showPhotoFilterDialog() {
        // get the view
        val filterDialogView = layoutInflater.inflate(
            R.layout.fillters_selector_view,
            view!!.findViewById(R.id.createFragment)
        )

        // get the rv
        val fontsRv = filterDialogView.filters_selector_view_rv
        //fontsRv.layoutManager = LinearLayoutManager(context!!, LinearLayoutManager.HORIZONTAL, false)

        // set the adapter and add all filters

        val pairs = ArrayList<Pair<PhotoFilter, Int>>()
        pairs.add(Pair(PhotoFilter.NONE, R.drawable.original))
        pairs.add(Pair(PhotoFilter.BRIGHTNESS, R.drawable.brightness))
        pairs.add(Pair(PhotoFilter.CONTRAST, R.drawable.contrast))
        pairs.add(Pair(PhotoFilter.CROSS_PROCESS, R.drawable.cross_process))
        pairs.add(Pair(PhotoFilter.DOCUMENTARY, R.drawable.documentary))
        pairs.add(Pair(PhotoFilter.DUE_TONE, R.drawable.dual_tone))
        pairs.add(Pair(PhotoFilter.FILL_LIGHT, R.drawable.fill_light))
        pairs.add(Pair(PhotoFilter.FISH_EYE, R.drawable.fish_eye))
        pairs.add(Pair(PhotoFilter.NEGATIVE, R.drawable.negative))
        pairs.add(Pair(PhotoFilter.SATURATE, R.drawable.saturate))
        pairs.add(Pair(PhotoFilter.VIGNETTE, R.drawable.vignette))

        val adapter = GroupAdapter<ViewHolder>()
        for (pair in pairs) {
            adapter.add(
                FilterItem(
                    image = pair.second, filterType = pair.first,
                    action = ::getDataFromFilterDialog
                )
            )
        }

        fontsRv.adapter = adapter

        // create the view and show it
        val dialog = AlertDialog.Builder(context!!).create()
        dialog.setView(filterDialogView)
        dialog.show()

    }

    // function to get data from filter Item when user click on it
    private fun getDataFromFilterDialog(filter: PhotoFilter) {
        photoEditor.setFilterEffect(filter)
    }

    // send image to firestore
    private fun sendImageStatus() {

        saveEditorImage() // generate image uri

        if (imageUri != null) { // upload the image
            uploadImage(imageUri!!)
        } else {
            Toast.makeText(
                context!!, resources.getString(R.string.statusImageUploadedFailed),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun uploadImage(imageUri: Uri) {

        // convert the uri to image path

        val bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, imageUri)
        val bAOS = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bAOS)
        val data = bAOS.toByteArray()
        val uuid = UUID.randomUUID().toString().substring(0, 16)
        val ref = FirebaseStorage.getInstance().reference.child("$uuid.png")

        ref.putBytes(data).addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener {
                saveImageUrl(it.toString(), uuid)
            }
        }
    }

    private fun saveImageUrl(url: String, imageID: String) {
        val ref = FirebaseFirestore.getInstance()
        val id = FirebaseAuth.getInstance().uid!!
        val image = UserStatusImage(
            url,
            imageID,
            Timestamp(System.currentTimeMillis()),
            id, ArrayList()
        )

        ref.collection("users_images").document(image.imageID).set(image).addOnSuccessListener {
            if (context == null || view == null) return@addOnSuccessListener
            Toast.makeText(
                context!!, getString(R.string.statusImageUploaded),
                Toast.LENGTH_LONG
            ).show()
            send_image_text_hint.visibility = View.VISIBLE
            send_image_image_view.setImageDrawable(context!!.getDrawable(R.drawable.rounded_send_image))
        }

    }

}
