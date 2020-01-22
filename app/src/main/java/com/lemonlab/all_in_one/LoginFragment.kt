package com.lemonlab.all_in_one


import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.lemonlab.all_in_one.extensions.hideKeypad
import com.lemonlab.all_in_one.extensions.highlightText
import com.lemonlab.all_in_one.extensions.navigateToAndClear
import com.lemonlab.all_in_one.extensions.showMessage
import com.lemonlab.all_in_one.items.Category
import com.lemonlab.all_in_one.items.CategoryPics
import com.lemonlab.all_in_one.items.categories
import kotlinx.android.synthetic.main.fragment_login.*
import kotlin.random.Random

/**
 * A fragment users can login from.
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

        init()
        super.onViewCreated(view, savedInstanceState)

    }

    private fun init() {
        val category = categories[Random.nextInt(categories.size)]
        login_quote_text_tv.text = context!!.highlightText(getRandomQuote(category))
        login_text_image.setImageResource(CategoryPics.getRandomPic(category))

        login_btn.setOnClickListener {
            val email = login_user_email_edit_text.text.toString()
            val password = login_user_password_edit_text.text.toString()
            // check if email or password is empty/invalid
            checkFields(email, password)
        }

        register_tv.setOnClickListener {
            it.findNavController().popBackStack()
            it.findNavController().navigate(R.id.registerFragment)
        }

    }

    private fun getRandomQuote(category: Category): String {
        val list = LocalQuotesFragment().getStatuses(
            category,

            resources
        )
        return list[Random.nextInt(list.size)]
    }


    private fun checkFields(userEmail: String, userPassword: String) {

        val emailOK = if (userEmail.contains('@'))
            Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()
        else
            false

        val passwordOK = userPassword.length >= 6
        if (!emailOK)
            login_user_email_edit_text.error = getString(R.string.invalidEmail)
        if (!passwordOK)
            login_user_password_edit_text.error = getString(R.string.invalidPassword)

        if (emailOK && passwordOK)
            login(email = userEmail, password = userPassword)

    }

    private fun login(email: String, password: String) {
        activity!!.hideKeypad()
        context!!.showMessage(getString(R.string.signing_in))
        loginProgressBar.visibility = View.VISIBLE
        login_btn.isEnabled = false
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val uid = it.user!!.uid
                if (uid.isNotEmpty())
                    FirebaseMessaging.getInstance().subscribeToTopic(uid)
                view!!.navigateToAndClear(R.id.loginFragment, R.id.mainFragment)
            }.addOnFailureListener {
                loginProgressBar.visibility = View.GONE
                if (it.localizedMessage!!.contains("no user"))
                    context!!.showMessage(getString(R.string.no_user))
                else
                    context!!.showMessage(getString(R.string.problem_occurred))
                login_btn.isEnabled = true
            }
    }
}
