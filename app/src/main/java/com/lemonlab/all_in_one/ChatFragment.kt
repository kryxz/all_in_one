package com.lemonlab.all_in_one


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.lemonlab.all_in_one.extensions.checkUser
import com.lemonlab.all_in_one.extensions.showMessage
import com.lemonlab.all_in_one.items.ChatItem
import com.lemonlab.all_in_one.model.Message
import com.lemonlab.all_in_one.model.User
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_chat.*
import java.sql.Timestamp

/**
Users can send and receive messages here.
 */

class ChatFragment : Fragment() {

    private val MAX_MESSAGES = 51

    private val adapter: GroupAdapter<ViewHolder> = GroupAdapter()
    private var currentUser: User? = null

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

        // get current user from firestore to use it when user send a message
        //TODO:: Add loading ui and disabled all ui until getting user complete
        getCurrentUser()
        listenToMessages()

        chat_rv.adapter = adapter

        send_message_btn.setOnClickListener {
            val messageText = chat_edit_text.text.toString()
            // clear the edit text
            chat_edit_text.text.clear()
            // add the message to rv adapter and store it in the database
            sendMessage(messageText)
        }
    }

    private fun getCurrentUser(){
        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid
        db.collection("users").document("$uid").get().addOnCompleteListener{
            if(it.isSuccessful){
                val document = it.result
                if(document != null && document.exists()){
                    val username = document.get("name").toString()
                    val email = document.get("email").toString()
                    currentUser = User(username, email)
                }
            }
        }
    }

    private fun sendMessage(text:String){
        val message = Message(
            text = text,
            username = currentUser!!.name, // temp code for test
            userUid = FirebaseAuth.getInstance().uid.toString(),
            timestamp = Timestamp(System.currentTimeMillis())
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
        val docRef = db.collection("chats").orderBy("timestamp")
        docRef.addSnapshotListener{
            snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }

            if(snapshot != null){
                // clear the adapter
                adapter.clear()
                // get all messages and clear the old
                var documents = snapshot.documents
                documents = deleteOldMessages(documents)
                for(doc in documents){
                    adapter.add(
                        ChatItem(
                            Message(
                                text = doc.data!!["text"].toString(),
                                userUid = "",
                                username = "",
                                timestamp =Timestamp(0)//TODO:: Some work here
                            ))
                    )
                }
            }
        }
    }

    private fun deleteOldMessages(documents: List<DocumentSnapshot>):List<DocumentSnapshot>{
        if(documents.size > MAX_MESSAGES){
            return documents.subList(documents.size - MAX_MESSAGES, documents.size - 1)
        }
        for(i in documents.size - MAX_MESSAGES downTo 0){
            val db = FirebaseFirestore.getInstance()
            db.collection("chats").document(documents[i].id).delete()
        }
        return documents
    }


}
