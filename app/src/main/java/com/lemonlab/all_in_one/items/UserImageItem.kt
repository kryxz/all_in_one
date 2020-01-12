package com.lemonlab.all_in_one.items

import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.google.firebase.auth.FirebaseAuth
import com.lemonlab.all_in_one.PicturesFragment
import com.lemonlab.all_in_one.PicturesFragmentDirections
import com.lemonlab.all_in_one.R
import com.lemonlab.all_in_one.extensions.showYesNoDialog
import com.lemonlab.all_in_one.model.UserStatusImage
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.user_image_item.view.*


class UserImageItem(
    private val image: UserStatusImage,
    private val adapter: GroupAdapter<ViewHolder>

) :

    Item<ViewHolder>() {

    override fun getLayout() =
        R.layout.user_image_item


    override fun bind(viewHolder: ViewHolder, position: Int) {
        val view = viewHolder.itemView
        val context = view.context
        val thisUserID = FirebaseAuth.getInstance().uid.toString()

        view.setOnClickListener {
            it.findNavController().navigate(PicturesFragmentDirections.ViewImageNow(image.url))
        }

        view.user_image_report.setOnClickListener {
            context.showYesNoDialog(
                functionToPerform = { image.report(thisUserID) },
                functionIfCancel = {},
                dialogTitle = context.getString(R.string.report_image),
                dialogMessage = context.getString(R.string.confirm_report_image)
            )
        }

        view.user_image_save.setOnClickListener {
            PicturesFragment.picturesViewModel.saveImage(image.url, context)
        }
        if (image.userId == thisUserID) {
            view.user_image_delete.visibility = View.VISIBLE
            view.user_image_delete.setOnClickListener {
                context.showYesNoDialog(
                    functionToPerform = {
                        adapter.remove(this)
                        image.deleteImage()
                    },
                    functionIfCancel = {},
                    dialogTitle = context.getString(R.string.delete_image),
                    dialogMessage = context.getString(R.string.confirm_delete_image)
                )
            }

        }
        CircularProgressDrawable(context).apply {
            strokeWidth = 12f
            centerRadius = 120f
            start()
            setColorSchemeColors(ContextCompat.getColor(context, R.color.colorPrimaryDark))
            Picasso.get().load(image.url).placeholder(this).into(view.user_image_image_view)
        }


    }


}