package com.teamnusocial.nusocial.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.ybq.android.spinkit.SpinKitView
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        viewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)

        // make the sign in button out of focus
        signInButton.alpha = 0.20f
        signInButton.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            overridePendingTransition(0, 0)
        }

        // validate email
        emailSignUpEditText.addTextChangedListener {
            viewModel.updateValidEmail(it.toString())
        }

        // validate password
        passwordSignUpEditText.addTextChangedListener {
            viewModel.updateValidPassword(it.toString())
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
            if (!it) {
                passwordSignUpEditText.setBackgroundResource(R.drawable.rounded_edit_text_error)
                Log.d("AUTH", "Invalid password")
            } else {
                passwordSignUpEditText.setBackgroundResource(R.drawable.rounded_edit_text)
            }
        })
        val spin_kit = findViewById<SpinKitView>(R.id.spin_kit)!!
        val bg_cover = findViewById<CardView>(R.id.loading_cover)!!
        continueSignUpButton.setOnClickListener { _ ->
            if (!(viewModel.isValidPassword.value!! && viewModel.isValidEmail.value!!)) {
                Snackbar
                    .make(
                        signUpRootView,
                        "Please enter valid email and password",
                        Snackbar.LENGTH_SHORT
                    )
                    .show()
            } else {
                spin_kit.visibility = View.VISIBLE
                bg_cover.visibility = View.VISIBLE
                val email = emailSignUpEditText.text.toString()
                val password = passwordSignUpEditText.text.toString()

                viewModel.createUser(email, password).addOnCompleteListener {
                    spin_kit.visibility = View.GONE
                    bg_cover.visibility = View.GONE
                    if (it.isSuccessful) {
                        // setup user and move to next activity
                        Log.d("AUTH", "Create User Success")
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.initializeUser(
                                it.result?.user!!.uid,
                                nameSignUpEditText.text.toString()
                            )
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