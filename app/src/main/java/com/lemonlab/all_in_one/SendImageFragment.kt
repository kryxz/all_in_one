package com.lemonlab.all_in_one


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.lemonlab.all_in_one.model.UserStatusImage
import kotlinx.android.synthetic.main.fragment_send_image.*
import java.io.ByteArrayOutputStream
import java.sql.Timestamp
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class SendImageFragment : Fragment() {

    val IMAGE_PICK_CODE = 1
    var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_send_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // select image from gallery
        send_image_image_view.setOnClickListener {
            selectImage()
        }

        // upload the image to firebase storage
        send_image_send_btn.setOnClickListener {
            uploadImage()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.isNotEmpty() && grantResults[0] ==
            PackageManager.PERMISSION_GRANTED){
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
            //system OS is < Marshmallow
            pickImageFromGallery()
        }
    }


    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            send_image_image_view.setImageURI(data?.data)
            imageUri = data?.data
            send_image_text_hint.visibility = View.INVISIBLE
        }
    }

    private fun uploadImage(){

        // convert the uri to image path
        val bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, imageUri)

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val uid = UUID.randomUUID()
        val ref = FirebaseStorage.getInstance().reference.child("$uid.png")

        ref.putBytes(data).addOnSuccessListener {
            saveImageUrl(it.uploadSessionUri.toString())
        }
    }

    private fun saveImageUrl(url:String){
        val ref = FirebaseFirestore.getInstance()
        val id = FirebaseAuth.getInstance().uid
        val image = UserStatusImage(url, Timestamp(System.currentTimeMillis()), id!!)

        ref.collection("users_images").add(image).addOnSuccessListener {
            Toast.makeText(context!!, resources.getString(R.string.statusImageUploaded), Toast.LENGTH_LONG)
                .show()
        }
    }
}
