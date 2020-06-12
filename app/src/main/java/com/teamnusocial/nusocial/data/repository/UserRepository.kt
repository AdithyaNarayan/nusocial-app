package com.teamnusocial.nusocial.data.repository

import android.location.Location
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.getField
import com.teamnusocial.nusocial.data.model.LocationLatLng
import com.teamnusocial.nusocial.data.model.User
import com.teamnusocial.nusocial.utils.FirestoreUtils
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class UserRepository(private val utils: FirestoreUtils) {
    suspend fun getUsers() = coroutineScope {
        val userList = listOf<User>().toMutableList()
        val currUser = getCurrentUserAsUser()
        utils.getAllUsers().get().addOnCompleteListener {
            if (it.isSuccessful) {
                it.result!!.documents.forEach { user ->
                   if(FirebaseAuth.getInstance().uid != user.id && !currUser.buddies.contains(user.id) && !currUser.seenAndMatch.contains(user.id)) {
                       var userItem = user.toObject(User::class.java)!!
                       userItem.uid = user.id
                       userList.add(userItem)
                   }
                }
            } else {
                Log.d("USER", it.exception!!.message.toString())
            }
        }.await()
        userList
    }
    suspend fun addUser(user: User) = coroutineScope {
        utils.getAllUsers().add(user)
    }

    suspend fun updateCurrentLocation(location: Location) = coroutineScope {
        utils
            .getAllUsers().document(utils.getCurrentUser()!!.uid)
            .update(
                "location",
                LocationLatLng(
                    location.latitude,
                    location.longitude
                )
            )
    }
    suspend fun updateCurrentBuddies(newBuddy: String) = coroutineScope {
        utils
            .getAllUsers().document(utils.getCurrentUser()!!.uid)
            .update(
                "buddies",
                FieldValue.arrayUnion(newBuddy)
            )
    }
    suspend fun updateCurrentMatches(newMatch: String) = coroutineScope {
        utils
            .getAllUsers().document(utils.getCurrentUser()!!.uid)
            .update(
                "seenAndMatch",
                FieldValue.arrayUnion(newMatch)
            )
    }
    suspend fun updateStringField(newValue: String, field: String) = coroutineScope {
        utils
            .getAllUsers().document(utils.getCurrentUser()!!.uid)
            .update(
                field,
                newValue
            )
    }
    suspend fun updateNumberField(newValue: Number, field: String) = coroutineScope {
        utils
            .getAllUsers().document(utils.getCurrentUser()!!.uid)
            .update(
                field,
                newValue
            )
    }

    suspend fun getCurrentUserAsDocument() = utils.getCurrentUserAsDocument()

    suspend fun getCurrentUserAsUser() = coroutineScope {
        var user = User()
        //Log.d("DEBUG_F", FirebaseAuth.getInstance().uid)
        utils.getCurrentUserAsDocument().get().addOnCompleteListener {
            if (it.isSuccessful) {
                user = it.result?.toObject(User::class.java)!!
            } else {
                Log.d("USER", it.exception!!.message.toString())
            }
        }.addOnFailureListener {
            Log.d("DEBUG_USER", it.message.toString())
        }
            .await()
            user
        }
    }