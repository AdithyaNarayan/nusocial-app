package com.teamnusocial.nusocial.ui.broadcast

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.teamnusocial.nusocial.data.model.User
import com.teamnusocial.nusocial.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class BroadcastViewModel(private val repository: UserRepository) : ViewModel() {

    var currentUserLocation = MutableLiveData(LatLng(0.0, 0.0))
    var broadcastRadius = MutableLiveData<Int>().apply {
        value = 200
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
            5 -> 1000
            6 -> 2000
            7 -> 3000
            else -> 100
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

}