package com.teamnusocial.nusocial.utils

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.teamnusocial.nusocial.data.model.*
import kotlinx.coroutines.coroutineScope

class FirestoreUtils {
    private val firestoreInstance = FirebaseFirestore.getInstance()

    private val firebaseAuth = FirebaseAuth.getInstance()

    suspend fun getAllUsers() =
        coroutineScope {
            firestoreInstance.collection("users")
        }

    suspend fun getOneUser(userID: String) = coroutineScope {
        var user: User = User()
        coroutineScope {
            user = firestoreInstance.collection("users").document(userID)
                .get().result?.toObject(User::class.java)!!
        }
        user
    }

    fun getCurrentUser() = firebaseAuth.currentUser
    suspend fun getCurrentUserAsUser() = coroutineScope {
        var user: User = User()
        coroutineScope {
            user = firestoreInstance.collection("users").document(getCurrentUser()!!.uid)
                .get().result?.toObject(User::class.java)!!
        }
        user

    }

    suspend fun getCurrentUserAsDocument() = coroutineScope {
        return@coroutineScope firestoreInstance
            .collection("users")
            .document(getCurrentUser()!!.uid)
    }


}