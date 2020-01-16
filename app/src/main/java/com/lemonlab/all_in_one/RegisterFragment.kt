package com.lemonlab.all_in_one


import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.lemonlab.all_in_one.extensions.navigateToAndClear
import com.lemonlab.all_in_one.extensions.removeWhitespace
import com.lemonlab.all_in_one.extensions.showMessage
import com.lemonlab.all_in_one.model.User
import kotlinx.android.synthetic.main.fragment_register.*

/**
 * A fragment users where can create an account.
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
        init()
        super.onViewCreated(view, savedInstanceState)

    }


    private fun init() {

        // register a new user in FireBase and store its information in fireStore
        register_btn.setOnClickListener {
            val email = user_email_edit_text.text.toString().removeWhitespace()
            val password = user_password_edit_text.text.toString().removeWhitespace()
            val username = username_edit_text.text.toString().removeWhitespace()
            checkFields(email, password, username)
        }

        // navigate to log in fragments
        log_in_tv.setOnClickListener {
            view!!.navigateToAndClear(R.id.registerFragment, R.id.loginFragment)
        }

    }

    private fun checkFields(userEmail: String, userPassword: String, username: String) {

        val emailOK = if (userEmail.contains('@'))
            Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()
        else
            false

        val passwordOK = userPassword.length >= 6

        val usernameOK = username.length > 5

        if (!emailOK)
            user_email_edit_text.error = getString(R.string.invalidEmail)

        if (!passwordOK)
            user_password_edit_text.error = getString(R.string.invalidPassword)

        if (!usernameOK)
            username_edit_text.error = getString(R.string.invalidUsername)

        if (emailOK && passwordOK && usernameOK)
            registerNewUser(email = userEmail, password = userPassword, name = username)

    }

    private fun registerNewUser(email: String, password: String, name: String) {
        val user = User(name = name, email = email, online = true)
        val auth = FirebaseAuth.getInstance()
        registerProgressBar.visibility = View.VISIBLE
        context!!.showMessage(getString(R.string.signing_in))
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                auth.currentUser!!.updateProfile(
                    UserProfileChangeRequest.Builder().setDisplayName(
                        name
                    ).build()
                )
                addUserToFireStore(user)
            }.addOnFailureListener {
                registerProgressBar.visibility = View.GONE
                context!!.showMessage(getString(R.string.problem_occurred))
            }

    }

    private fun addUserToFireStore(user: User) {
        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid!!
        db.collection("users").document(uid).set(user).addOnSuccessListener {
            view!!.navigateToAndClear(R.id.registerFragment, R.id.mainFragment)
        }.addOnFailureListener {
            registerProgressBar.visibility = View.GONE
            context!!.showMessage(getString(R.string.problem_occurred))
        }
    }

}
