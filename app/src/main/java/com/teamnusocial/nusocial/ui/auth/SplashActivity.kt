package com.teamnusocial.nusocial.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.teamnusocial.nusocial.HomeActivity
import com.teamnusocial.nusocial.R
import kotlinx.android.synthetic.main.activity_splash.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

class SplashActivity : AppCompatActivity(), KodeinAware {

    override val kodein by closestKodein()
    private val factory by instance<AuthViewModelFactory>()
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        viewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)

        // Move to main activity if already signed in
        viewModel.isSignedIn().observe(this, Observer {
            if (it) {
                startActivity(Intent(this@SplashActivity, HomeActivity::class.java))
            }
        })

        joinButton.setOnClickListener {
            startActivity(Intent(this@SplashActivity, SignInActivity::class.java))
        }
    }
}