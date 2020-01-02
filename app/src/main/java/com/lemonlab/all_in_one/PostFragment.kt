package com.lemonlab.all_in_one


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_post.*


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
        clearTextOnClick()
        super.onViewCreated(view, savedInstanceState)
    }


    private fun clearTextOnClick() {

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

    }


}
