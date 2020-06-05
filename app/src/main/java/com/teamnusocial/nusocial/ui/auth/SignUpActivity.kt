package com.teamnusocial.nusocial.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.teamnusocial.nusocial.HomeActivity
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.utils.FirestoreUtils
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance


class SignUpActivity : AppCompatActivity(), KodeinAware {

    override val kodein by closestKodein()
    private val factory by instance<AuthViewModelFactory>()
    private lateinit var viewModel: AuthViewModel

    private fun isValidEmail() =
        !emailSignUpEditText.text.isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(
            emailSignUpEditText.text.toString()
        ).matches()


    private fun isValidPassword() =
        !passwordSignUpEditText.text.isNullOrEmpty() && passwordSignUpEditText.text.toString().length >= 6

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // make the sign in button out of focus
        signInButton.alpha = 0.20f
        signInButton.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            overridePendingTransition(0, 0)
        }

        viewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)

        // validate email
        emailSignUpEditText.addTextChangedListener {
            viewModel.isValidEmail.value = isValidEmail()
        }

        // validate password
        passwordSignUpEditText.addTextChangedListener {
            viewModel.isValidPassword.value = isValidPassword()
        }

        // change the edit text depending on validity of email
        viewModel.isValidEmail.observe(this@SignUpActivity, Observer {
            if (!it) {
                emailSignUpEditText.setBackgroundResource(R.drawable.rounded_edit_text_error)
                Log.d("AUTH", "Invalid email")
            } else {
                emailSignUpEditText.setBackgroundResource(R.drawable.rounded_edit_text)
            }
        })

        // change the edit text depending on validity of password
        viewModel.isValidPassword.observe(this@SignUpActivity, Observer {
            if (!isValidPassword()) {
                passwordSignUpEditText.setBackgroundResource(R.drawable.rounded_edit_text_error)
                Log.d("AUTH", "Invalid password")
            } else {
                passwordSignUpEditText.setBackgroundResource(R.drawable.rounded_edit_text)
            }
        })

        continueSignUpButton.setOnClickListener { _ ->

            if (!(isValidEmail() && isValidPassword())) {
                Snackbar
                    .make(
                        signUpRootView,
                        "Please enter valid email and password",
                        Snackbar.LENGTH_SHORT
                    )
                    .show()
            } else {
                val email = emailSignUpEditText.text.toString()
                val password = passwordSignUpEditText.text.toString()

                viewModel.createUser(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        // setup user and move to next activity
                        Log.d("AUTH", "Create User Success")
                        CoroutineScope(Dispatchers.IO).launch {
                            FirestoreUtils().initializeUser(it.result?.user?.uid!!)
                        }
                        startActivity(Intent(this@SignUpActivity, HomeActivity::class.java))
                    } else {
                        // display an error message to the user.
                        Log.d("AUTH", it.exception?.message.toString())
                        Snackbar
                            .make(
                                signUpRootView,
                                it.exception?.message.toString(),
                                Snackbar.LENGTH_SHORT
                            )
                            .show()
                    }
                }
            }
        }
    }

}