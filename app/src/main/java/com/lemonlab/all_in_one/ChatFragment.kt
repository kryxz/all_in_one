package com.lemonlab.all_in_one


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lemonlab.all_in_one.extensions.checkUser
import com.lemonlab.all_in_one.items.ChatItem
import com.lemonlab.all_in_one.model.Message
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.chat_item.*
import kotlinx.android.synthetic.main.fragment_chat.view.*

/**
Users can send and receive messages here.
 */

class ChatFragment : Fragment() {

    private val adapter: GroupAdapter<ViewHolder> = GroupAdapter()

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

        // init
        listenToMessages()
        view.chat_rv.adapter = adapter

        view.send_message_btn.setOnClickListener {
            sendMessage(chat_edit_text.text.toString())
        }
    }

    private fun sendMessage(text:String){
        // TODO:: Save the messages in the firestore
        // TODO:: Get username
        val message = Message(
            text = text,
            username = "zaka", // temp code for test
            userUid = FirebaseAuth.getInstance().uid.toString()
        )
        val chatItem = ChatItem(
            message = message
        )

        adapter.add(chatItem)

        val db = FirebaseFirestore.getInstance()
        db.collection("chats").add(message)
    }

    private fun listenToMessages(){
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("chats")
        docRef.addSnapshotListener{
            snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }

            if(snapshot != null){
                // clear the adapter
                adapter.clear()
                // get all messages
                for(doc in snapshot.documents.reversed()){
                    adapter.add(
                        ChatItem(
                            Message(
                                text = doc.data!!["text"].toString(),
                                userUid = "",
                                username = ""
                            ))
                    )
                }
            }
        }
    }


}
