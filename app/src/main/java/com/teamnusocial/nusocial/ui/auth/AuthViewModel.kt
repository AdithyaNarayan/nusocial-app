package com.teamnusocial.nusocial.ui.auth

import android.util.Log
import android.util.Patterns
import androidx.core.util.PatternsCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.teamnusocial.nusocial.data.repository.AuthUserRepository
import kotlinx.android.synthetic.main.activity_sign_up.*

// Common ViewModel for SplashActivity, SignInActivity and SignUpActivity
class AuthViewModel(private var repository: AuthUserRepository) : ViewModel() {

    val isValidEmail = MutableLiveData<Boolean>()
    val isValidPassword = MutableLiveData<Boolean>()

    fun updateValidEmail(email: String) {
        isValidEmail.apply {
            value = email.isNotEmpty() && PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()
        }
    }


    fun updateValidPassword(password: String) {
        isValidPassword.value = password.isNotEmpty() && password.length >= 6
    }

    fun isSignedIn() = MutableLiveData(repository.isSignedIn())

    fun createUser(email: String, password: String) = repository.createUser(email, password)

    fun signInUser(email: String, password: String) = repository.signInUser(email, password)

    fun forgotPassword(email: String) = repository.forgotPassword(email)

    suspend fun initializeUser(userID: String, name: String) =
        repository.initializeUser(userID, name)
}
