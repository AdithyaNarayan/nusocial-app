package com.teamnusocial.nusocial.ui.auth

import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.teamnusocial.nusocial.data.repository.AuthUserRepository

// Common ViewModel for SplashActivity, SignInActivity and SignUpActivity
class AuthViewModel(private var repository: AuthUserRepository) : ViewModel() {

    val isValidEmail = MutableLiveData<Boolean>()
    val isValidPassword = MutableLiveData<Boolean>()

    fun isSignedIn() = repository.isSignedIn()

    fun createUser(email: String, password: String) = repository.createUser(email, password)

    fun signInUser(email: String, password: String) = repository.signInUser(email, password)

    fun forgotPassword(email: String) = repository.forgotPassword(email)
}
