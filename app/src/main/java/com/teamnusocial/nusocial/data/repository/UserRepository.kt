package com.teamnusocial.nusocial.data.repository

import android.location.Geocoder
import android.location.Location
import android.util.Log
import com.teamnusocial.nusocial.data.model.LocationLatLng
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


}
