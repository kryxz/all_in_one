package com.lemonlab.all_in_one.extensions

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.text.SpannableString
import android.text.format.DateFormat
import android.text.style.BackgroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.google.android.gms.ads.*
import com.google.android.gms.ads.doubleclick.PublisherAdRequest
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.lemonlab.all_in_one.MainActivity
import com.lemonlab.all_in_one.R
import com.lemonlab.all_in_one.items.UnifiedAd
import com.lemonlab.all_in_one.model.StatusColor
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import es.dmoral.toasty.Toasty
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*
import kotlin.concurrent.timerTask

// asks user to login to continue.
fun View.checkUser() =
    FirebaseAuth.getInstance().currentUser
        ?: findNavController().navigate(R.id.mustLoginFragment)


fun View.navigateToAndClear(destinationId: Int, newDes: Int) {
    val navOptions = NavOptions.Builder().setPopUpTo(destinationId, true).build()
    this.findNavController().navigate(
        newDes,
        null, navOptions
    )
}

fun Context.showMessage(m: String) {
    val sharedPrefs = getSharedPreferences("UserPrefs", 0)
    val mainColor = sharedPrefs
        .getInt("mainColor", StatusColor.Blue.value)
    val tintColor = ContextCompat.getColor(this, mainColor)
    val textColor = ContextCompat.getColor(this, R.color.white)
    Toasty.custom(
        this, m, null, tintColor,
        textColor, 100, false, true
    ).show()

}


fun userOnline() {
    val uid = FirebaseAuth.getInstance().uid
    // check if there user logged in
    if (!uid.isNullOrEmpty()) {
        val db = FirebaseFirestore.getInstance()

        db.collection("users").document("$uid")
            .update("online", true)

        // store the data in the real time database
        FirebaseDatabase.getInstance().getReference("status/$uid").setValue("online")

        // Adding on disconnect hook
        FirebaseDatabase.getInstance().getReference("/status/$uid")
            .onDisconnect()     // Set up the disconnect hook
            .setValue("offline")

    }
}


// Highlights text background.
fun Context.highlightText(text: String): SpannableString {
    val sharedPrefs = getSharedPreferences("UserPrefs", 0)

    val mainColor = sharedPrefs.getInt("mainColor", StatusColor.Blue.value)

    val str = SpannableString(text)
    str.setSpan(
        BackgroundColorSpan(ContextCompat.getColor(this, mainColor)),
        0,
        text.length,
        0
    )
    return str
}


fun Context.highlightTextWithColor(color: Int, text: String): SpannableString {
    val str = SpannableString(text)
    str.setSpan(
        BackgroundColorSpan(ContextCompat.getColor(this, color)),
        0,
        text.length,
        0
    )
    return str
}


fun Activity.createImageFile(): File {
    // Create an image file name
    val uuid: String =
        getString(R.string.app_name) + UUID.randomUUID().toString().substring(0, 16)
    val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(
        uuid, ".jpg",
        storageDir /* directory */
    )
}


fun Activity.setFragmentTitle(text: String) {
    (this as AppCompatActivity).supportActionBar!!.title = text
}

fun View.getBitmapFromView(): Bitmap {
    // Define a bitmap with the same size as the view
    val returnedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    // Bind a canvas to it
    val canvas = Canvas(returnedBitmap)
    // Get the view's background
    val bgDrawable = this.background
    if (bgDrawable != null)
    // has background drawable, then draw it on the canvas
        bgDrawable.draw(canvas)
    else
    // does not have background drawable, then draw white background on the canvas
        canvas.drawColor(Color.WHITE)
    // draw the view on the canvas
    this.draw(canvas)
    //return the bitmap
    return returnedBitmap
}


fun View.recreateFragment(fragmentID: Int) =
    findNavController().navigate(
        fragmentID,
        null,
        NavOptions.Builder()
            .setPopUpTo(fragmentID, true)
            .build()
    )


fun Context.showYesNoDialog(
    functionToPerform: () -> Unit,
    functionIfCancel: () -> Unit,
    dialogTitle: String,
    dialogMessage: String
) {

    val dialogBuilder = AlertDialog.Builder(this).create()
    val dialogView = with(LayoutInflater.from(this)) {
        inflate(
            R.layout.yes_no_dialog,
            null
        )
    }

    dialogView.findViewById<AppCompatTextView>(R.id.dialogTitle).text = dialogTitle
    dialogView.findViewById<AppCompatTextView>(R.id.dialogMessageText).text = dialogMessage

    dialogView.findViewById<AppCompatButton>(R.id.dialogCancelButton).setOnClickListener {
        functionIfCancel()
        dialogBuilder.dismiss()
    }

    dialogView.findViewById<AppCompatButton>(R.id.dialogConfirmButton).setOnClickListener {
        functionToPerform()
        dialogBuilder.dismiss()
    }

    with(dialogBuilder) {
        setView(dialogView)
        show()
    }

}


fun String.removeWhitespace(): String {
    var isFirstSpace = false
    var result = ""
    for (char in this) {
        if (char != ' ' && char != '\n') {
            isFirstSpace = true
            result += char
        } else if (isFirstSpace) {
            result += " "
            isFirstSpace = false
        }
    }
    return result
}

fun Activity.hideKeypad() =
    with(getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager) {
        hideSoftInputFromWindow(window.decorView.rootView.windowToken, 0)
    }


fun Activity.showKeypad() =
    with(getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager) {
        toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }


fun getDateAsString(date: Date) =
    DateFormat.format("yyyy-MM-dd hh:mm", date)!!


fun Context.getImageUriFromBitmap(bitmap: Bitmap): Uri {
    val bytes = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes)
    val path = MediaStore.Images.Media.insertImage(
        contentResolver,
        bitmap, getString(R.string.app_name), null
    )
    return Uri.parse(path.toString())
}


fun addAd(index: Int, adapter: GroupAdapter<ViewHolder>) {
    if (index % 5 == 0)
        Ads.addAd(adapter)
}

fun Context.scanFile(filePath: String) {
    val path = arrayOf(filePath)
    MediaScannerConnection.scanFile(this, path, null)
    { _, _ -> }

}

// handles ads in app
class Ads {

    companion object {

        fun loadFullScreenAd(ad: InterstitialAd) {
            if (ad.adUnitId.isNullOrEmpty())
                ad.adUnitId = "ca-app-pub-9769401692194876/7852805560"
            ad.loadAd(AdRequest.Builder().build())
        }

        private var ad: UnifiedNativeAd? = null

        fun addAd(adapter: GroupAdapter<ViewHolder>) {
            if (ad != null)
                adapter.add(UnifiedAd(ad!!))
        }

        fun loadAd(context: Context) {

            // load new ad every 16 seconds.

            Timer().schedule(timerTask { ad = null; loadAd(context) }, 16000)
            if (ad != null) return

            val videoOptions = VideoOptions.Builder()
                .setStartMuted(true)
                .build()

            val adOptions = NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build()
            val adID = "ca-app-pub-9769401692194876/3354323205"
            val adLoader = AdLoader.Builder(context, adID)
                .forUnifiedNativeAd { ad -> this.ad = ad }

                .withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(errorCode: Int) {
                        // Handle the failure by logging, altering the UI, and so on.
                    }

                }).withNativeAdOptions(adOptions).build()

            adLoader.loadAd(PublisherAdRequest.Builder().build())
        }
    }
}

const val adminUID = "d2I64q3CgwcUAaRVoYaepDNEHpu1"


fun Context.askThenSave(bitmap: Bitmap) {

    val uuid = UUID.randomUUID().toString().substring(0, 8)

    Dexter.withActivity(MainActivity.instance!!)

        .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        .withListener(object : PermissionListener {
            override fun onPermissionGranted(response: PermissionGrantedResponse) {
                val path = MediaStore.Images.Media.insertImage(
                    contentResolver,
                    bitmap,
                    uuid,
                    uuid + getString(R.string.app_name)
                )
                if (path != null)
                    scanFile(path)

                showMessage(getString(R.string.image_saved))
            }

            override fun onPermissionDenied(response: PermissionDeniedResponse) {}

            override fun onPermissionRationaleShouldBeShown(
                permission: PermissionRequest?,
                token: PermissionToken?
            ) {
            }

        }).check()

}