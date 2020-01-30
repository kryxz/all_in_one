package com.lemonlab.all_in_one


import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.gms.ads.InterstitialAd
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lemonlab.all_in_one.extensions.Ads
import com.lemonlab.all_in_one.extensions.removeWhitespace
import com.lemonlab.all_in_one.extensions.showMessage
import com.lemonlab.all_in_one.model.ForumPost
import kotlinx.android.synthetic.main.fragment_post.*
import java.sql.Timestamp
import java.util.*
import kotlin.collections.ArrayList


/**
 * Users create and post to the forum here.
 */


class PostFragment : Fragment() {

    private val fullScreenAd: InterstitialAd by lazy {
        InterstitialAd(context!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initButtons()
        Ads.loadFullScreenAd(fullScreenAd)
    }


    private fun initButtons() {

        clear_post_text_btn.setOnClickListener {

            // do nothing if fields are empty
            if (forum_post_text.text.isNullOrBlank() && forum_post_title.text.isNullOrBlank()) return@setOnClickListener

            // save text for un delete
            val postText = forum_post_text.text.toString().removeWhitespace()
            val titleText = forum_post_title.text.toString().removeWhitespace()

            // clear text fields
            forum_post_text.text!!.clear()
            forum_post_title.text!!.clear()

            // shows a snackBar with undo(restore deleted text) action.
            val black = ContextCompat.getColor(context!!, R.color.black)
            Snackbar.make(view!!, getString(R.string.textDeleted), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.undo)) {
                    forum_post_text.setText(postText)
                    forum_post_title.setText(titleText)
                }
                .setActionTextColor(black).setTextColor(black)
                .show()
        }
        preview_forum_post_btn.setOnClickListener {
            // do nothing if fields are empty
            if (forum_post_text.text.isNullOrBlank() || forum_post_title.text.isNullOrBlank()) return@setOnClickListener


            // declare builder and view
            val dialogBuilder = AlertDialog.Builder(context!!).create()
            val dialogView = layoutInflater.inflate(
                R.layout.preview_post_dialog,
                view!!.findViewById(R.id.postFragment)
            )


            // declare views
            val titleTextView = dialogView.findViewById<AppCompatTextView>(R.id.preview_forum_title)
            val postTextView = dialogView.findViewById<AppCompatTextView>(R.id.preview_forum_text)
            val okayButton = dialogView.findViewById<AppCompatButton>(R.id.preview_post_done_btn)


            // show dialog
            dialogBuilder.setView(dialogView)
            dialogBuilder.show()

            // get and set text
            val postText = forum_post_text.text.toString().removeWhitespace()
            val titleText = forum_post_title.text.toString().removeWhitespace()

            val previewText = if (postText.length > 64)
                StringBuilder().append(postText.substring(0, 63)).append("…").toString()
            else
                postText

            postTextView.text = previewText
            titleTextView.text = titleText

            // dismiss dialog on click
            okayButton.setOnClickListener {
                dialogBuilder.dismiss()
            }
        }

        post_to_forum_btn.setOnClickListener {

            if (forum_post_text.text.isNullOrBlank() || forum_post_title.text.isNullOrBlank()) {
                context!!.showMessage(getString(R.string.fillFields))
                return@setOnClickListener
            }

            // create a Forum Post object
            val postText = forum_post_text.text.toString().removeWhitespace()
            val titleText = forum_post_title.text.toString().removeWhitespace()

            val auth = FirebaseAuth.getInstance()
            val uid = auth.uid.toString()
            val timestamp = Timestamp(System.currentTimeMillis())
            val userName = auth.currentUser!!.displayName
            val postID =
                "$userName-" + UUID.randomUUID().toString().substring(0, 16).replace("-", "")

            val forumPost = ForumPost(
                title = titleText,
                text = postText,
                userID = uid,
                timestamp = timestamp,
                comments = ArrayList(),
                likesIDs = ArrayList(),
                dislikesIDs = ArrayList(),
                reportIDs = ArrayList(),
                postID = postID
            )
            val db = FirebaseFirestore.getInstance()
            forumPostingProgressBar.visibility = View.VISIBLE
            postFragmentView.visibility = View.GONE

            fullScreenAd.show()

            db.collection("posts").document(postID).set(forumPost).addOnSuccessListener {
                forumPostingProgressBar.visibility = View.GONE
                postFragmentView.visibility = View.VISIBLE
                // clear text fields
                forum_post_text.text!!.clear()
                forum_post_title.text!!.clear()
                context!!.showMessage(getString(R.string.postSent))
                // go back
                view!!.findNavController().navigateUp()
            }

        }

    }


}
