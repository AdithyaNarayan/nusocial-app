package com.teamnusocial.nusocial.data.repository

import android.location.Location
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.teamnusocial.nusocial.data.model.LocationLatLng
import com.teamnusocial.nusocial.data.model.User
import com.teamnusocial.nusocial.utils.FirestoreUtils
import kotlinx.coroutines.coroutineScope
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

    suspend fun updateCurrentLocation(location: Location) = coroutineScope {
        utils
            .getAllUsers()
            .document(utils.getCurrentUser()!!.uid)
            .update(
                "location",
                LocationLatLng(
                    location.latitude,
                    location.longitude
                )
            )
    }

}
