package com.teamnusocial.nusocial.ui.buddymatch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.squareup.picasso.Picasso
import com.teamnusocial.nusocial.data.model.User
import kotlinx.coroutines.coroutineScope

class BuddyMatchViewModel : ViewModel() {

    val images: ArrayList<String> = arrayListOf()

    var matchedUsers: MutableLiveData<MutableList<User>> = MutableLiveData(mutableListOf())
    var you: MutableLiveData<User> = MutableLiveData(User())

    fun updateMatchedUsers(list: MutableList<User>) {
        matchedUsers.value = list.toMutableList()
        if (list.size > 0) {
            matchedUsers.notifyListener()
        }
    }
    fun updateYou(user: User) {
        you.value = user
    }
}

private fun <T> MutableLiveData<T>.notifyListener() {
    this.value = this.value
}

