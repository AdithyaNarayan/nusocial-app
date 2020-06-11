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
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.model.User
import com.teamnusocial.nusocial.data.model.knn.Classifier
import com.teamnusocial.nusocial.data.repository.UserRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.net.URL


class BroadcastViewModel(private val repository: UserRepository) : ViewModel() {

    init {
        var classifier = Classifier()
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

    var closestNeighboursList: MutableLiveData<MutableList<User>> = MutableLiveData(mutableListOf())
    var currentUserLocation = MutableLiveData(LatLng(0.0, 0.0))
    var broadcastRadius = MutableLiveData<Int>().apply {
        value = 200
    }
    var profileImg = MutableLiveData<Bitmap>().apply {
        value = null
    }
    private val _text = MutableLiveData<String>().apply {
        value = "Map goes here"
    }
    val text: LiveData<String> = _text

    suspend fun getUsers() = coroutineScope {
        repository.getUsers()
    }

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
}

private fun <T> MutableLiveData<T>.notifyListener() {
    this.value = this.value
}