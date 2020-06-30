package com.teamnusocial.nusocial.ui.broadcast

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.model.User
import com.teamnusocial.nusocial.data.model.knn.Classifier
import com.teamnusocial.nusocial.data.repository.UserRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.net.URL
import java.time.LocalDate


class BroadcastViewModel(private val repository: UserRepository) : ViewModel() {

    init {

    }

    var closestNeighboursList: MutableLiveData<MutableList<User>> = MutableLiveData(mutableListOf())
    var currentUserLocation = MutableLiveData(LatLng(0.0, 0.0))
    var broadcastRadius = MutableLiveData<Int>().apply {
        value = 200
    }
    var profileImg = MutableLiveData<Bitmap>()

    suspend fun getNeighbours() = coroutineScope {
        val classifier = Classifier()
        CoroutineScope(Dispatchers.IO).launch {
            val currentUser = repository.getCurrentUserAsUser()
            classifier.populateDataPoints(repository.getUsers(), currentUser)
            withContext(Dispatchers.Main) {
                closestNeighboursList.value = classifier.classifyDataPoint(currentUser)
                Log.d("BROADCAST", closestNeighboursList.value.toString())
                closestNeighboursList.notifyListener()
            }
        }
    }
    suspend fun getUsers() = repository.getUsers()

    fun updateRadius(progress: Int) {
        broadcastRadius.value = when (progress) {
            0 -> 100
            1 -> 200
            2 -> 300
            3 -> 400
            4 -> 500
            else -> 200
        }
    }

    fun updateCurrentLocation() {
        CoroutineScope(Dispatchers.Main).launch {
            repository.getCurrentUserAsDocument().addSnapshotListener { snapshot, _ ->
                currentUserLocation.value =
                    snapshot!!.toObject(User::class.java)?.location!!.getAsLatLng()
            }
        }
    }

    suspend fun getCurrentUserAsUser() = repository.getCurrentUserAsUser()

    fun setProfileBitmap(bitmap: Bitmap) {
        profileImg.value = bitmap
    }

    suspend fun sendBroadcast(messageText: String) = coroutineScope {
        repository.updateLastBroadcasted(Timestamp.now())
        repository.getUserAnd(repository.getCurrentUserAsUser().uid) { currUser ->
            closestNeighboursList.value!!.filter { otherUser ->
                currUser.location.distanceTo(otherUser.location) < (broadcastRadius.value!! - 50)
            }.forEach {
                CoroutineScope(Dispatchers.IO).launch {
                    repository.createChatWith(it.uid)
                    repository.sendMessage(repository.getMessageID(currUser, it), messageText)
                    repository.makeInvisibleTo(currUser.uid, repository.getMessageID(currUser, it))
                }
            }
        }
    }
}

private fun <T> MutableLiveData<T>.notifyListener() {
    this.value = this.value
}