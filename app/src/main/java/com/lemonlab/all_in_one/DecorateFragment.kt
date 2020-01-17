package com.lemonlab.all_in_one

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.lemonlab.all_in_one.extensions.getBitmapFromView
import com.lemonlab.all_in_one.extensions.highlightText
import com.lemonlab.all_in_one.extensions.highlightTextWithColor
import com.lemonlab.all_in_one.items.Category
import com.lemonlab.all_in_one.items.CategoryPics
import com.lemonlab.all_in_one.items.QuoteItem
import com.lemonlab.all_in_one.model.StatusColor
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.change_font_dialog.view.*
import kotlinx.android.synthetic.main.change_image_dialog.view.*
import kotlinx.android.synthetic.main.fragment_decorate.*
import kotlinx.android.synthetic.main.image_item_view_dialog.view.*


class DecorateFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_decorate, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }


    private fun init() {
        val args = DecorateFragmentArgs.fromBundle(arguments!!)

        val category = Category.valueOf(args.category)
        val text = args.textDecorate

        decorate_status_image.setImageResource(CategoryPics.getRandomPic(category))
        decorate_text_edit_text.setText(context!!.highlightText(text))
        // font
        changeFontText.setOnClickListener {
            changeFontDialog()
        }


        changeBackgroundImage.setOnClickListener {
            changeImageDialog()
        }

        // save image
        text_withImage_save.setOnClickListener {
            val bitmap = textLayoutView.getBitmapFromView()
            QuoteItem.saveImage(bitmap, context!!)
        }

        // color spinner
        val colorsTexts = resources.getStringArray(R.array.statusColors)

        text_color_spinner.adapter = ArrayAdapter(
            context!!,
            R.layout.app_spinner, colorsTexts
        )

        val colors = listOf(
            StatusColor.Blue,
            StatusColor.Red,
            StatusColor.Green,
            StatusColor.Black
        )


        text_color_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val theText = decorate_text_edit_text.text.toString()
                val highlightedText = context!!.highlightTextWithColor(
                    colors[position]
                        .value, theText
                )
                decorate_text_edit_text.setText(highlightedText)
            }

        }

    }


    private fun changeFontDialog() {

        val dialogBuilder = AlertDialog.Builder(context).create()
        val dialogView = with(LayoutInflater.from(context)) {
            inflate(
                R.layout.change_font_dialog, null
            )

        }
        val fontCairo = dialogView.fontCairo
        val fontMada = dialogView.fontMada
        val fontTaj = dialogView.fontTajawal

        val fontAlMar = dialogView.fontAlMarai


        val fonts = listOf(fontCairo, fontMada, fontTaj, fontAlMar)

        fun changeFont(font: Int) {
            val type = ResourcesCompat.getFont(context!!, font)!!
            decorate_text_edit_text.typeface = type
        }

        dialogView.fontChangeCancelButton.setOnClickListener {
            dialogBuilder.dismiss()
        }

        for (fontButton in fonts)
            fontButton.setOnClickListener {
                when (it) {

                    fontCairo ->
                        changeFont(R.font.cairo_regular)

                    fontMada ->
                        changeFont(R.font.mada)

                    fontTaj ->
                        changeFont(R.font.tajawal_regular)

                    fontAlMar ->
                        changeFont(R.font.almarai)

                }
                dialogBuilder.dismiss()
            }


        with(dialogBuilder) {
            setView(dialogView)
            show()
        }

    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 1)
            decorate_status_image.setImageURI(data!!.data)

    }

    private fun changeImageDialog() {
        val category = Category.valueOf(DecorateFragmentArgs.fromBundle(arguments!!).category)
        val dialogBuilder = AlertDialog.Builder(context).create()
        val dialogView = with(LayoutInflater.from(context)) {
            inflate(R.layout.change_image_dialog, null)
        }

        val imagesRV = dialogView.images_rv
        val adapter = GroupAdapter<ViewHolder>()
        val images = CategoryPics.getPics(category)
        for (image in images)
            adapter.add(ImageItemPick(image, dialogBuilder))

        imagesRV.adapter = adapter


        dialogView.cancel_image_choose.setOnClickListener {
            dialogBuilder.dismiss()
        }
        dialogView.gallery_choose.setOnClickListener {
            dialogBuilder.dismiss()
            pickImageFromGallery()
        }

        val listOfImages = CategoryPics.getAllPics()
        dialogView.show_AllImages.setOnClickListener {
            adapter.clear()
            for (theImages in listOfImages)
                for (image in theImages)
                    adapter.add(ImageItemPick(image, dialogBuilder))

        }
        with(dialogBuilder) {
            setView(dialogView)
            show()
        }

    }


    fun changePic(resourceID: Int) =
        decorate_status_image.setImageResource(resourceID)


    inner class ImageItemPick(private val resourceID: Int, private val dialog: AlertDialog) :

        Item<ViewHolder>() {
        override fun getLayout() =
            R.layout.image_item_view_dialog

        override fun bind(viewHolder: ViewHolder, position: Int) {
            val view = viewHolder.itemView
            view.item_dialog_image.setImageResource(resourceID)
            view.setOnClickListener {
                changePic(resourceID)
                dialog.dismiss()
            }

        }

    }

}
