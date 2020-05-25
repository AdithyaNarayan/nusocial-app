package com.teamnusocial.nusocial.data.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.teamnusocial.nusocial.utils.FirebaseAuthUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class AuthUserRepository(private val authUtils: FirebaseAuthUtils) {

    fun isSignedIn(): MutableLiveData<Boolean> =
        MutableLiveData<Boolean>().apply {
            value = authUtils.getCurrentUser() != null
        }


    fun createUser(email: String, password: String) = authUtils.createUser(email, password)

    fun signInUser(email: String, password: String) = authUtils.signInUser(email, password)

    fun forgotPassword(email: String) = authUtils.forgotPassword(email)
}