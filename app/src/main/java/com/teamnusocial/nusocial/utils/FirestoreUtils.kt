package com.teamnusocial.nusocial.utils

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.teamnusocial.nusocial.data.model.Gender
import com.teamnusocial.nusocial.data.model.LocationLatLng
import com.teamnusocial.nusocial.data.model.User
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await


class FirestoreUtils {
    private val firestoreInstance = FirebaseFirestore.getInstance()

    private val firebaseAuth = FirebaseAuth.getInstance()

    suspend fun getAllUsers() = coroutineScope {
        firestoreInstance.collection("users")
    }

    fun getCurrentUser() = firebaseAuth.currentUser

    suspend fun getCurrentUserAsDocument() = coroutineScope {
        return@coroutineScope firestoreInstance
            .collection("users")
            .document(getCurrentUser()!!.uid)
    }

}