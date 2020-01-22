package com.lemonlab.all_in_one


import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.lemonlab.all_in_one.extensions.checkUser
import com.lemonlab.all_in_one.items.ChatItem
import com.lemonlab.all_in_one.items.ChatViewModel
import com.lemonlab.all_in_one.model.Message
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_chat.*
import java.sql.Timestamp

/**
Users can send and receive messages here.
 */

class ChatFragment : Fragment() {


    private val adapter: GroupAdapter<ViewHolder> = GroupAdapter()
    private var onlineUsersCount: Int = 0

    companion object {
        lateinit var chatViewModel: ChatViewModel
        lateinit var lifecycleOwner: LifecycleOwner
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.checkUser()
        super.onViewCreated(view, savedInstanceState)

        init()

    }

    private fun init() {
        chatViewModel = ViewModelProviders.of(this).get(ChatViewModel::class.java)
        lifecycleOwner = this
        chat_rv.adapter = adapter
        slideToLastMessage()

        // observe messages
        chatViewModel.getMessages().observe(this, Observer {
            adapter.clear()
            for (message in it)
                adapter.add(ChatItem(message))

            slideToLastMessage()
        })


        // observe online users count
        chatViewModel.getOnlineCount().observe(this, Observer {
            onlineUsersCount = it
        })


        send_message_btn.setOnClickListener {
            sendMessage()
        }

        // hide replay view
        chat_rv.setOnClickListener {
            //  chat_replay_view.visibility = View.GONE
        }

    }

    private fun sendMessage() {

        val messageText = chat_edit_text.text.toString()
        if (messageText.isEmpty()) return
        send_message_btn.setBackgroundColor(ContextCompat.getColor(context!!, R.color.greyDark))
        // clear the edit text
        chat_edit_text.text!!.clear()

        val message = Message(
            text = messageText,
            username = chatViewModel.getUsername(), // temp code for test
            userUid = chatViewModel.getUserID(),
            timestamp = Timestamp(System.currentTimeMillis())
        )

        // 2 seconds delay between messages
        send_message_btn.setOnClickListener(null)
        Handler().postDelayed({
            if (view != null) {
                send_message_btn.setBackgroundColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.colorPrimaryDark
                    )
                )
                send_message_btn.setOnClickListener { sendMessage() }
            }
        }, 2000)

        chatViewModel.sendMessage(message)
        slideToLastMessage()

    }


/*
    private fun deleteOldMessages(documents: List<DocumentSnapshot>): List<DocumentSnapshot> {
        val maxMessages = 51
        if (documents.size > maxMessages) {
            return documents.subList(documents.size - maxMessages, documents.size - 1)
        }
        for (i in documents.size - maxMessages downTo 0) {
            val db = FirebaseFirestore.getInstance()
            db.collection("chats").document(documents[i].id).delete()
        }
        return documents
    }
 */


    private fun slideToLastMessage() {
        if (adapter.itemCount >= 1)
            chat_rv.scrollToPosition(adapter.itemCount - 1)
    }


    private fun onLongLickChatItemTrigger(message: Message) {
        //chat_replay_view.visibility = View.VISIBLE
    }
}
