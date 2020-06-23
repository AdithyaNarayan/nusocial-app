package com.teamnusocial.nusocial.utils

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.teamnusocial.nusocial.data.model.Gender
import com.teamnusocial.nusocial.data.model.LocationLatLng
import com.teamnusocial.nusocial.data.model.User
import kotlinx.coroutines.coroutineScope
import java.time.LocalDate
import java.util.*

class FirebaseAuthUtils {
    private val firebaseAuth = FirebaseAuth.getInstance()

    private val firestoreInstance = FirebaseFirestore.getInstance()

    fun createUser(email: String, password: String) =
        firebaseAuth.createUserWithEmailAndPassword(email, password)

    fun signInUser(email: String, password: String) =
        firebaseAuth.signInWithEmailAndPassword(email, password)

    fun getCurrentUser() = firebaseAuth.currentUser

    fun forgotPassword(email: String) = firebaseAuth.sendPasswordResetEmail(email)

    fun logOut() = firebaseAuth.signOut()

    suspend fun initializeUser(userID: String, name: String) = coroutineScope {
        firestoreInstance.collection("users").document(userID).set(
            User(
                userID,
                name,
                Gender.MALE,
                "https://i7.pngflow.com/pngimage/455/105/png-anonymity-computer-icons-anonymous-user-anonymous-purple-violet-logo-smiley-clipart.png",
                arrayListOf(),
                1,
                "--blank--",
                LocationLatLng(0.0, 0.0, ""),
                arrayListOf(),
                "Hey there! I'm using NUSocial!",
                Timestamp(Date(0)),
                mutableListOf(),
                mutableListOf(),
                mutableListOf()
            )
        )
    }

}