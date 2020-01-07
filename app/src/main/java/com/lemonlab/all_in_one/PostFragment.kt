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
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lemonlab.all_in_one.extensions.showMessage
import com.lemonlab.all_in_one.model.ForumPost
import kotlinx.android.synthetic.main.fragment_post.*
import java.sql.Timestamp


/**
 * Users create and post to the forum here.
 */


class PostFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initButtons()
        super.onViewCreated(view, savedInstanceState)
    }


    private fun initButtons() {

        clear_post_text_btn.setOnClickListener {

            // do nothing if fields are empty
            if (forum_post_text.text.isNullOrBlank() && forum_post_title.text.isNullOrBlank()) return@setOnClickListener

            // save text for un delete
            val postText = forum_post_text.text.toString()
            val titleText = forum_post_title.text.toString()

            // clear text fields
            forum_post_text.text!!.clear()
            forum_post_title.text!!.clear()

            // shows a snackBar with undo(restore deleted text) action.
            Snackbar.make(view!!, getString(R.string.textDeleted), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.undo)) {
                    forum_post_text.setText(postText)
                    forum_post_title.setText(titleText)
                }
                .setActionTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.white
                    )
                )
                .show()
        }
        preview_forum_post_btn.setOnClickListener {
            // do nothing if fields are empty
            if (forum_post_text.text.isNullOrBlank() && forum_post_title.text.isNullOrBlank()) return@setOnClickListener


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
            val postText = forum_post_text.text.toString()
            val titleText = forum_post_title.text.toString()

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
            val postText = forum_post_text.text.toString()
            val titleText = forum_post_title.text.toString()

            val uid = FirebaseAuth.getInstance().uid.toString()
            val timestamp = Timestamp(System.currentTimeMillis())
            val forumPost = ForumPost(
                title = titleText,
                text = postText,
                userID = uid,
                timestamp = timestamp,
                comments = null,
                likesIDs = null,
                dislikesIDs = null,
                reports = 0,
                reportIDs = null,
                postID = ""
            )
            val db = FirebaseFirestore.getInstance()
            forumPostingProgressBar.visibility = View.VISIBLE
            db.collection("posts").add(forumPost).addOnSuccessListener {
                forumPostingProgressBar.visibility = View.GONE
                // clear text fields
                forum_post_text.text!!.clear()
                forum_post_title.text!!.clear()
                context!!.showMessage(getString(R.string.postSent))
                forumPost.postID = it.id
                db.collection("posts").document(it.id).set(forumPost)
                // go back
                view!!.findNavController().navigateUp()
            }

        }

    }


}
