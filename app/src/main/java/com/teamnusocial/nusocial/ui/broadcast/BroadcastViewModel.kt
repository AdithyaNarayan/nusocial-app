package com.teamnusocial.nusocial.ui.broadcast

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.teamnusocial.nusocial.data.model.User
import com.teamnusocial.nusocial.data.repository.UserRepository
import kotlinx.coroutines.coroutineScope

class BroadcastViewModel(private val repository: UserRepository) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Map goes here"
    }
    val text: LiveData<String> = _text

    suspend fun getUsers() = coroutineScope {
        repository.getUsers()
    }
}