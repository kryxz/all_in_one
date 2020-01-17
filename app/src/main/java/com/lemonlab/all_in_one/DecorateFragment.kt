package com.lemonlab.all_in_one

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Typeface
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
import com.lemonlab.all_in_one.items.FontItem
import com.lemonlab.all_in_one.items.QuoteItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.change_image_dialog.view.*
import kotlinx.android.synthetic.main.fonts_selector_view.view.*
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
        val colorsTexts = resources.getStringArray(R.array.MoreStatusColors)

        val adapter = ArrayAdapter(
            context!!,
            R.layout.app_spinner, colorsTexts
        )
        background_color_spinner.adapter = adapter


        val colors = listOf(
            R.color.darkBlue,
            R.color.magentaPurple,
            R.color.darkGreen,
            R.color.superBlack,
            R.color.rose,
            R.color.gold,
            R.color.emerald,
            R.color.semiBrown,
            R.color.charcoal,
            R.color.silver
        )

        background_color_spinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val theText = decorate_text_edit_text.text.toString()
                    val highlightedText = context!!.highlightTextWithColor(
                        colors[position], theText
                    )
                    decorate_text_edit_text.setText(highlightedText)
                }

            }

    }


    private fun changeFontDialog() {

        fun changeFont(typeface: Typeface) {
            decorate_text_edit_text.typeface = typeface
        }


        val dialogBuilder = AlertDialog.Builder(context).create()

        val dialogView =
            with(LayoutInflater.from(context)) {
                inflate(R.layout.fonts_selector_view, null)
            }

        val fonts = listOf(
            getFont(R.font.amiri_regular),
            getFont(R.font.aram),
            getFont(R.font.mada),
            getFont(R.font.almarai),
            getFont(R.font.baloobhaijaan_regular),
            getFont(R.font.cairo_regular),
            getFont(R.font.lalezar_regular),
            getFont(R.font.rakkas_regular),
            getFont(R.font.tajawal_regular),
            getFont(R.font.vibes_regular)
        )

        dialogView.fonts_selector_view_btn.setOnClickListener {
            dialogBuilder.dismiss()
        }


        val adapter = GroupAdapter<ViewHolder>()
        // add all fonts to the adapter
        for (font in fonts) {
            adapter.add(
                FontItem(
                    fontFace = font, action = ::changeFont, dialog = dialogBuilder
                )
            )
        }

        // set the adapter
        dialogView.fonts_rv.adapter = adapter


        with(dialogBuilder) {
            setView(dialogView)
            show()
        }


    }

    private fun getFont(id: Int) = ResourcesCompat.getFont(context!!, id)!!


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
