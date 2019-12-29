package com.lemonlab.all_in_one


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lemonlab.all_in_one.extensions.navigateToAndClear
import com.lemonlab.all_in_one.extensions.showMessage
import com.lemonlab.all_in_one.model.User
import kotlinx.android.synthetic.main.fragment_register.view.*

/**
 * A simple [Fragment] subclass.
 */
class RegisterFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // register a new user in firebase and store its information in firestore
        view.register_btn.setOnClickListener{
            val email = view.useremail_edittext.text.toString()
            val password = view.user_password_edittext.text.toString()
            val username = view.username_edit_text.text.toString()
            registerNewUser(email = email, password = password, name = username)
        }

        // navigate to log in fragments
        view.log_in_tv.setOnClickListener {
            view.navigateToAndClear(R.id.registerFragment, R.id.loginFragment)
        }

    }

    private fun registerNewUser(email: String, password: String, name:String){
        val user = User(name = name, email = email)
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{
                addUserToFirestore(user)
                context!!.showMessage("تم تسجيل الدخول بنجاح")
                view!!.navigateToAndClear(R.id.registerFragment, R.id.mainFragment)
            }.addOnFailureListener{
                context!!.showMessage("حدثت مشكلة اثناء الاتصال")
            }
    }

    private fun addUserToFirestore(user: User){
        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid
        db.collection("users").document("$uid").set(user)
    }

}
