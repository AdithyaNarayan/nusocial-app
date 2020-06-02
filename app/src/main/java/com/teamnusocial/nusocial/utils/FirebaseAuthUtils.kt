package com.teamnusocial.nusocial.utils

import com.google.firebase.auth.FirebaseAuth

class FirebaseAuthUtils {

    private val firebaseAuth = FirebaseAuth.getInstance()

    fun createUser(email: String, password: String) =
        firebaseAuth.createUserWithEmailAndPassword(email, password)

    fun signInUser(email: String, password: String) =
        firebaseAuth.signInWithEmailAndPassword(email, password)

    fun getCurrentUser() = firebaseAuth.currentUser

    fun forgotPassword(email: String) = firebaseAuth.sendPasswordResetEmail(email)

}