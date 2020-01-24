package com.lemonlab.all_in_one


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.storage.FirebaseStorage
import com.lemonlab.all_in_one.extensions.checkUser
import com.lemonlab.all_in_one.extensions.getImageUriFromBitmap
import com.lemonlab.all_in_one.extensions.showMessage
import com.lemonlab.all_in_one.model.UserStatusImage
import kotlinx.android.synthetic.main.fragment_send_image.*
import java.io.ByteArrayOutputStream
import java.sql.Timestamp
import java.util.*
import kotlin.collections.ArrayList


/**
 * A fragment where users can upload images.
 */

class SendImageFragment : Fragment() {

    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_send_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.checkUser()
        init()
    }

    private fun init() {
        val bitmap = SendImageFragmentArgs.fromBundle(arguments!!).image
        if (bitmap != null) {
            imageUri = context!!.getImageUriFromBitmap(bitmap)
            send_image_image_view.setImageURI(imageUri)
            send_image_text_hint.visibility = View.INVISIBLE
            send_image_image_view.alpha = 1f
        }

        // select image from gallery
        send_image_image_view.setOnClickListener {
            selectImage()
        }

        // upload the image to fireBase storage
        send_image_send_btn.setOnClickListener {
            if (imageUri == null) {
                selectImage()
                return@setOnClickListener
            }
            processImage()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.isNotEmpty() && grantResults[0] ==
            PackageManager.PERMISSION_GRANTED
        ) {
            //permission from popup granted
            pickImageFromGallery()
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
            // system OS is < Marshmallow
            pickImageFromGallery()
        }
    }


    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            send_image_image_view.setImageURI(data!!.data)
            send_image_image_view.alpha = 1.0f
            imageUri = data.data
            send_image_text_hint.visibility = View.INVISIBLE
        }
    }

    private fun uploadImage(bitmap: Bitmap) {

        // convert the uri to image path

        val bAOS = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bAOS)
        val data = bAOS.toByteArray()
        val username = FirebaseAuth.getInstance().currentUser!!.displayName
        val uuid = "$username-" + UUID.randomUUID().toString().substring(0, 16)
        val ref = FirebaseStorage.getInstance().reference.child("$uuid.png")

        ref.putBytes(data).addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener {
                saveImageUrl(it.toString(), uuid)
            }
        }
    }

    private fun processImage() {
        sendImageProgressBar.visibility = View.VISIBLE
        val notAllowed = getString(R.string.notAllowed)
        val vision = FirebaseVision.getInstance()
        val labeler = vision.onDeviceImageLabeler

        val image = FirebaseVisionImage.fromFilePath(context!!, imageUri!!)
        val bitmap = image.bitmap
        labeler.processImage(image)
            .addOnSuccessListener {
                for (label in it)
                    if (label.text in notAllowed)
                        imageUri = null

                if (imageUri != null) uploadImage(bitmap)
                else cannotUpload()
            }.addOnFailureListener {
                context!!.showMessage(getString(R.string.cannot_process_image))
            }

    }

    private fun cannotUpload() {
        val context = context!!
        sendImageProgressBar.visibility = View.GONE
        send_image_text_hint.visibility = View.VISIBLE
        with(send_image_image_view) {
            alpha = 0.5f
            setImageDrawable(context.getDrawable(R.drawable.rounded_send_image))
        }
        context.showMessage(getString(R.string.image_not_allowed))

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
            sendImageProgressBar.visibility = View.GONE
            context!!.showMessage(getString(R.string.statusImageUploaded))
            imageUri = null
            view!!.findNavController().navigate(R.id.picturesFragment)
            send_image_text_hint.visibility = View.VISIBLE
            with(send_image_image_view) {
                alpha = 0.5f
                setImageDrawable(context!!.getDrawable(R.drawable.rounded_send_image))
            }
        }

    }
}
