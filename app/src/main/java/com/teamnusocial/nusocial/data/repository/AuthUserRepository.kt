package com.teamnusocial.nusocial.data.repository

import androidx.lifecycle.MutableLiveData
import com.teamnusocial.nusocial.utils.FirebaseAuthUtils

class AuthUserRepository(private val authUtils: FirebaseAuthUtils) {

    fun isSignedIn(): MutableLiveData<Boolean> =
        MutableLiveData<Boolean>().apply {
            value = authUtils.getCurrentUser() != null
        }

    fun createUser(email: String, password: String) = authUtils.createUser(email, password)

    fun signInUser(email: String, password: String) = authUtils.signInUser(email, password)

    fun forgotPassword(email: String) = authUtils.forgotPassword(email)
}