package com.lemonlab.all_in_one.items

import android.app.Application
import android.content.Context
import android.graphics.PorterDuff
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.firebase.firestore.Query
import com.lemonlab.all_in_one.ChatFragment
import com.lemonlab.all_in_one.FireStoreRepository
import com.lemonlab.all_in_one.R
import com.lemonlab.all_in_one.extensions.highlightText
import com.lemonlab.all_in_one.model.Message
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.chat_item.view.*

class ChatItem(private val message: Message) : Item<ViewHolder>() {
    override fun getLayout() =
        R.layout.chat_item

    private val model = ChatFragment.chatViewModel

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val view = viewHolder.itemView
        // set the message text and the username
        val context = view.context
        view.chat_tv.text = context.highlightText(message.text)
        val textView = viewHolder.itemView.chat_item_username_tv

        textView.text = message.username


        // mark this message with new color
        if (message.userUid == model.getUserID())
            textView.setTextColor(ContextCompat.getColor(context, R.color.green))
        else
            textView.setTextColor(ContextCompat.getColor(context, R.color.red))

        model.getUserStatus(message.userUid).observe(ChatFragment.lifecycleOwner, Observer {
            if (it)
                tintDrawable(context, textView, R.color.green)
            else
                tintDrawable(context, textView, R.color.red)

        })

    }

    private fun tintDrawable(context: Context, textView: AppCompatTextView, colorID: Int) {

        var drawable = ContextCompat.getDrawable(context, R.drawable.circular_color)
        drawable = DrawableCompat.wrap(drawable!!)
        DrawableCompat.setTint(drawable, ContextCompat.getColor(context, colorID))
        DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN)
        textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)

    }

}


class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FireStoreRepository()


    fun getUserStatus(userID: String): LiveData<Boolean> {
        val isOnline: MutableLiveData<Boolean> = MutableLiveData()
        repository.getUsersRef().document(userID).addSnapshotListener { snapshot, e ->
            if (e != null) return@addSnapshotListener
            if (snapshot == null) return@addSnapshotListener
            if (snapshot.data == null) return@addSnapshotListener

            isOnline.value = snapshot["online"].toString().toBoolean()

        }
        return isOnline
    }


    fun getMessages(): LiveData<List<Message>> {
        val allMessages: MutableLiveData<List<Message>> = MutableLiveData()

        repository.getMessagesRef().orderBy(
            "timestamp",
            Query.Direction.DESCENDING
        ).limitToLast(50).addSnapshotListener { snapshot, e ->
            if (e != null) return@addSnapshotListener
            if (snapshot == null) return@addSnapshotListener
            val list = mutableListOf<Message>()
            val messages = snapshot.documents
            for (message in messages) {
                if (message.data == null) continue
                list.add(message.toObject(Message::class.java)!!)

            }

            allMessages.value = list.asReversed()
        }

        return allMessages
    }

    fun getOnlineCount(): LiveData<Int> {
        val count: MutableLiveData<Int> = MutableLiveData()
        repository.getUsersRef().whereEqualTo("online", "true").addSnapshotListener { snapshot, e ->
            if (e != null) return@addSnapshotListener
            if (snapshot == null) return@addSnapshotListener
            count.value = snapshot.documents.size

        }

        return count
    }

    fun sendMessage(message: Message) {
        val ref = repository.getMessagesRef()
        ref.add(message).addOnCompleteListener {
            ref.orderBy("timestamp", Query.Direction.ASCENDING).get().addOnSuccessListener {
                val count = it.documents.size
                if (count < 50) return@addOnSuccessListener
                val documents = it.documents
                for (i in documents.size - 50 downTo 0)
                    ref.document(documents[i].id).delete()
            }
        }

    }

    fun getUserID() =
        repository.getUserID()


    fun getUsername() =
        repository.auth.currentUser!!.displayName.toString()


}
