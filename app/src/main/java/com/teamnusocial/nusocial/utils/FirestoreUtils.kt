package com.teamnusocial.nusocial.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.teamnusocial.nusocial.data.model.Gender
import com.teamnusocial.nusocial.data.model.LocationLatLng
import com.teamnusocial.nusocial.data.model.User
import kotlinx.coroutines.coroutineScope


class FirestoreUtils {
    private val firestoreInstance = FirebaseFirestore.getInstance()

    private val firebaseAuth = FirebaseAuth.getInstance()

    suspend fun initializeUser(userID: String) = coroutineScope {
        firestoreInstance.collection("users").document(userID).set(
            User(
                "n0b0n",
                Gender.MALE,
                arrayListOf(),
                1,
                "CS",
                LocationLatLng(0.0, 0.0),
                arrayListOf(),
                "hello"
            )
        )
    }

    fun getCurrentUser() = firebaseAuth.currentUser.to(User::class.java)

    suspend fun getAllUsers() = coroutineScope {
        firestoreInstance.collection("users").get()
    }
}