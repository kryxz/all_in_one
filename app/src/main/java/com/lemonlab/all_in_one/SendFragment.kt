package com.lemonlab.all_in_one


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lemonlab.all_in_one.extensions.checkUser
import com.lemonlab.all_in_one.model.UserStatus
import com.lemonlab.all_in_one.model.UserStatusType
import kotlinx.android.synthetic.main.fragment_send.*
import java.sql.Timestamp

/*
    Users can send texts.
 */

class SendFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_send, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.checkUser()
        super.onViewCreated(view, savedInstanceState)

        send_status_send_btn.setOnClickListener {
            sendTextUserStatus()
        }

    }

    private fun sendTextUserStatus(){
        val text = send_status_edit_text.text.toString()
        val id = FirebaseAuth.getInstance().uid

        if(text.isEmpty()){
            Toast.makeText(context!!, resources.getString(R.string.warningTextEmpty), Toast.LENGTH_LONG).show()
        }

        val userState = UserStatus(
            text = text,
            statusType = UserStatusType.TextStatus,
            timestamp = Timestamp(System.currentTimeMillis()),
            userId = id.toString()
        )

        val db = FirebaseFirestore.getInstance()
        db.collection("usersStatus").add(userState).addOnSuccessListener {
            Toast.makeText(context!!, resources.getString(R.string.statusSent), Toast.LENGTH_LONG).show()
        }.addOnFailureListener{
            Toast.makeText(context!!, resources.getString(R.string.warningSentStatus), Toast.LENGTH_LONG).show()
        }
    }

}
