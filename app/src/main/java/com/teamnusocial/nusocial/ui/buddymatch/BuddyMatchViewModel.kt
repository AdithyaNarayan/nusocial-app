package com.teamnusocial.nusocial.ui.buddymatch

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.teamnusocial.nusocial.data.model.User
import com.teamnusocial.nusocial.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

class BuddyMatchViewModel(val repo: UserRepository) : ViewModel() {
    var matchedUsers: MutableLiveData<MutableList<User>> = MutableLiveData(mutableListOf())
    var images: MutableList<String> = mutableListOf()
    var you: MutableLiveData<User> = MutableLiveData(User())
    suspend fun updateMatchedUsers() = coroutineScope {
        val list = repo.getUsers().toMutableList()
        withContext(Dispatchers.Main) {
            if (list.size > 0) {
                matchedUsers.value = list
                matchedUsers.notifyListener()
            }
        }
    }
    suspend fun updateYou() = coroutineScope {
        withContext(Dispatchers.Main) {
            you.value = repo.getCurrentUserAsUser()
        }
    }
}

private fun <T> MutableLiveData<T>.notifyListener() {
    this.value = this.value
}

