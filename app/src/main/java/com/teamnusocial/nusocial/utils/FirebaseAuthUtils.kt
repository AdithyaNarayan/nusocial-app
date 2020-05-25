package com.teamnusocial.nusocial.utils

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class FirebaseAuthUtils {

    private val firebaseAuth = FirebaseAuth.getInstance()

    fun createUser(email: String, password: String) =
        firebaseAuth.createUserWithEmailAndPassword(email, password)

    fun signInUser(email: String, password: String) =
        firebaseAuth.signInWithEmailAndPassword(email, password)

    fun getCurrentUser() = firebaseAuth.currentUser

    fun forgotPassword(email: String) = firebaseAuth.sendPasswordResetEmail(email)

}