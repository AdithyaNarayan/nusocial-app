package com.teamnusocial.nusocial.ui.auth

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.teamnusocial.nusocial.HomeActivity
import com.teamnusocial.nusocial.R
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.layout_forgot_password.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

class SignInActivity : AppCompatActivity(), KodeinAware {

    override val kodein by closestKodein()
    private val factory by instance<AuthViewModelFactory>()
    private lateinit var viewModel: AuthViewModel

    private fun buildDialogForgotPassword() {
        @SuppressLint("InflateParams")
        val dialogView = LayoutInflater
            .from(this@SignInActivity)
            .inflate(R.layout.layout_forgot_password, null)

        MaterialAlertDialogBuilder(this@SignInActivity)
            .setTitle("Forgot Password")
            .setView(dialogView)
            .setMessage("Please enter your email address so that we can sent a reset link")
            .setPositiveButton(R.string.cont) { dialog, _ ->
                // send request for email
                val email = dialogView.findViewById<EditText>(R.id.emailForgotEditText).text

                if (email.isNullOrEmpty()) {
                    Snackbar
                        .make(
                            signInRootView,
                            "Please enter valid email",
                            Snackbar.LENGTH_SHORT
                        )
                        .show()
                } else {
                    viewModel.forgotPassword(email.toString()).addOnCompleteListener {
                        Log.d("AUTH", "Forgot Password Success")
                        if (it.isSuccessful) {
                            Snackbar
                                .make(
                                    signInRootView,
                                    "Reset email has been sent!",
                                    Snackbar.LENGTH_SHORT
                                )
                                .show()
                            dialog.cancel()
                        } else {
                            Log.d("AUTH", "Forgot Password Failed")
                            Snackbar
                                .make(
                                    signInRootView,
                                    it.exception?.message.toString(),
                                    Snackbar.LENGTH_SHORT
                                )
                                .show()
                        }
                    }
                }
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.cancel()
            }
            .create()
            .show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        // make the sign up button out of focus
        signUpButton.alpha = 0.20f
        signUpButton.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            overridePendingTransition(0, 0)
        }

        viewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)

        continueSignInButton.setOnClickListener { _ ->
            val email = emailSignInEditText.text.toString()
            val password = passwordSignInEditText.text.toString()

            if (email.isEmpty() or password.isEmpty()) {
                Snackbar
                    .make(
                        signInRootView,
                        "Please enter email and password",
                        Snackbar.LENGTH_SHORT
                    )
                    .show()
            } else {
                viewModel.signInUser(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        // move to next activity
                        Log.d("AUTH", "Sign In User Success")
                        startActivity(Intent(this@SignInActivity, HomeActivity::class.java))
                    } else {
                        // display an error message to the user.
                        Log.d("AUTH", it.exception?.message.toString())
                        Snackbar
                            .make(
                                signInRootView,
                                it.exception?.message.toString(),
                                Snackbar.LENGTH_SHORT
                            )
                            .show()
                    }
                }
            }

        }

        nusnetSignInButton.setOnClickListener {
            startActivity(Intent(this@SignInActivity, LumiNUSActivity::class.java))
        }


        forgotPasswordSignInButton.setOnClickListener {
            buildDialogForgotPassword()
        }

    }
}