package com.lemonlab.all_in_one


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.lemonlab.all_in_one.extensions.checkUser
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


    private val adapter: GroupAdapter<ViewHolder> = GroupAdapter()
    private var currentUser: User? = null
    private var onlineUsersCount: Int = 0

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

        // get current user from fireStore to use it when user send a message
        //TODO:: Add loading ui and disabled all ui until getting user complete
        getCurrentUser()
        getUsersOnline()

        chat_rv.adapter = adapter
        slideToLastMessage()

        send_message_btn.setOnClickListener {
            val messageText = chat_edit_text.text.toString()
            if (messageText.isEmpty()) return@setOnClickListener
            // clear the edit text
            chat_edit_text.text!!.clear()
            // add the message to rv adapter and store it in the database
            sendMessage(messageText)
        }

        // hide replay view
        chat_rv.setOnClickListener {
            chat_replay_view.visibility = View.GONE
        }
    }

    override fun onResume() {
        slideToLastMessage()
        super.onResume()
    }

    override fun onPause() {
        adapter.clear()
        super.onPause()
    }

    private fun getCurrentUser() {
        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid
        db.collection("users").document("$uid").get().addOnCompleteListener {
            if (it.isSuccessful) {
                val document = it.result
                if (document != null && document.exists()) {
                    val username = document.get("name").toString()
                    val email = document.get("email").toString()
                    val userStatus = document.get("online").toString()
                    currentUser = User(username, email, userStatus)
                }
                listenToMessages()
            }
        }
    }

    private fun sendMessage(text: String) {
        Log.i("sendMessage", "sending: $text")
        val message = Message(
            text = text,
            username = currentUser!!.name, // temp code for test
            userUid = FirebaseAuth.getInstance().uid.toString(),
            timestamp = Timestamp(System.currentTimeMillis())
        )
        val chatItem = ChatItem(
            message = message,
            context = context!!
        )

        adapter.add(chatItem)
        slideToLastMessage()

        val db = FirebaseFirestore.getInstance()
        db.collection("chats").add(message)
    }

    private fun listenToMessages() {

        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("chats").orderBy("timestamp")
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }

            if (context == null) return@addSnapshotListener

            if (snapshot != null) {
                // clear the adapter
                adapter.clear()
                // get all messages and clear the old
                var documents = snapshot.documents
                documents = deleteOldMessages(documents)
                for (doc in documents) {
                    adapter.add(
                        ChatItem(
                            Message(
                                text = doc.data!!["text"].toString(),
                                userUid = doc.data!!["userUid"].toString(),
                                username = doc.data!!["username"].toString(),
                                timestamp = Timestamp(0)//TODO:: Some work here
                            ),
                            context = context!!
                        )
                    )
                }
            }
        }
    }

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

    private fun slideToLastMessage() {
        if (adapter.itemCount >= 1)
            chat_rv.scrollToPosition(adapter.itemCount - 1)
    }

    private fun getUsersOnline() {
        val db = FirebaseFirestore.getInstance()
        db.collection("users").whereEqualTo("online", "true").addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            if (snapshot != null)
                onlineUsersCount = snapshot.documents.size
        }
    }

    private fun onLongLickChatItemTrigger(message: Message){
        chat_replay_view.visibility = View.VISIBLE
    }
}
