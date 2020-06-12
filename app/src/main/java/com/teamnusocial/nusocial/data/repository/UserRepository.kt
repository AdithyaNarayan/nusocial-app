package com.teamnusocial.nusocial.data.repository

import android.location.Geocoder
import android.location.Location
import android.util.Log
import com.google.firebase.Timestamp
import com.teamnusocial.nusocial.data.model.LocationLatLng
import com.teamnusocial.nusocial.data.model.TextMessage
import com.teamnusocial.nusocial.data.model.User
import com.teamnusocial.nusocial.utils.FirestoreUtils
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await


class UserRepository(private val utils: FirestoreUtils) {
    suspend fun getUsers() = coroutineScope {
        val userList = listOf<User>().toMutableList()
        utils.getAllUsers().get().addOnCompleteListener {
            if (it.isSuccessful) {
                it.result!!.documents.forEach { user ->
                    userList.add(user.toObject(User::class.java)!!)
                }
            } else {
                Log.d("USER", it.exception!!.message.toString())
            }
        }.await()
        userList
    }

    suspend fun updateCurrentLocation(location: Location, cluster: String) = coroutineScope {
        utils
            .getAllUsers()
            .document(utils.getCurrentUser()!!.uid)
            .update(
                "location",
                LocationLatLng(
                    location.latitude,
                    location.longitude,
                    cluster
                )
            )
    }


    suspend fun getCurrentUserAsDocument() = utils.getCurrentUserAsDocument()

    suspend fun getCurrentUserAsUser() = coroutineScope {
        var user = User()
        utils.getCurrentUserAsDocument().get().addOnCompleteListener {
            if (it.isSuccessful) {
                user = it.result?.toObject(User::class.java)!!
            } else {
                Log.d("USER", it.exception!!.message.toString())
            }
        }.await()
        user
    }

    suspend fun getUser(userID: String) = coroutineScope {
        var user = User()
        utils.getUserAsDocument(userID).get().addOnCompleteListener {
            if (it.isSuccessful) {
                user = it.result?.toObject(User::class.java)!!
            } else {
                Log.d("USER", it.exception!!.message.toString())
            }
        }.await()
        user
    }

    suspend fun getUserAnd(userID: String, onComplete: (User) -> Unit) {
        utils.getUserAsDocument(userID).get().addOnCompleteListener {
            if (it.isSuccessful) {
                onComplete(it.result!!.toObject(User::class.java)!!)
            }
        }
    }

    suspend fun createChatWith(userID: String) = utils.createChatWith(userID)

    suspend fun sendMessage(messageID: String, messageText: String) = coroutineScope {
        utils.getMessages(messageID).collection("messages")
            .add(
                TextMessage(
                    messageText,
                    Timestamp.now(),
                    utils.getCurrentUser()!!.uid
                )
            )
        utils.getMessages(messageID).collection("messages").addSnapshotListener { querySnapshot, exception ->
            if(querySnapshot!!.documents.size > 1) {
                utils.getMessages(messageID).update("invisible","")
            }
        }
    }

    suspend fun makeInvisibleTo(userID: String, messageID: String) = coroutineScope {
        utils.getMessages(messageID).update("invisible", userID)
    }

    fun getMessageID(firstUser: User, secondUser: User) =
        if (firstUser.uid > secondUser.uid) "${firstUser.uid}_${secondUser.uid}" else "${secondUser.uid}_${firstUser.uid}"
}
