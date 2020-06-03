package com.teamnusocial.nusocial.utils

import com.google.firebase.firestore.FirebaseFirestore
import com.teamnusocial.nusocial.data.model.Gender
import com.teamnusocial.nusocial.data.model.User
import kotlinx.coroutines.coroutineScope


class FirestoreUtils {
    private val firestoreInstance = FirebaseFirestore.getInstance()

    suspend fun initializeUser(userID: String) = coroutineScope {
        firestoreInstance.collection("users").document(userID).set(
            User(
                "n0b0n",
                Gender.MALE,
                arrayListOf(),
                1,
                "CS",
                null,
                arrayListOf(),
                "hello"
            )
        )
    }
}