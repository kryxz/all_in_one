package com.lemonlab.all_in_one


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.lemonlab.all_in_one.extensions.navigateToAndClear
import com.lemonlab.all_in_one.extensions.showMessage
import kotlinx.android.synthetic.main.fragment_login.view.*

/**
 * A simple [Fragment] subclass.
 */
class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.log_in_btn.setOnClickListener {
            val email = view.useremail_edittext.text.toString()
            val password = view.user_password_edittext.text.toString()
            logInUser(email = email, password = password);
        }

        view.sign_up_tv.setOnClickListener {
            view.navigateToAndClear(R.id.loginFragment, R.id.registerFragment)
        }
    }

    private fun logInUser(email:String, password:String){
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener{
            context!!.showMessage("تم تسجيل الدخول بنجاح")
            view!!.navigateToAndClear(R.id.loginFragment, R.id.mainFragment)
        }
    }
}
