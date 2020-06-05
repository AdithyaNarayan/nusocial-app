package com.teamnusocial.nusocial.ui.broadcast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.teamnusocial.nusocial.data.repository.UserRepository

@Suppress("UNCHECKED_CAST")
class BroadcastViewModelFactory(private val repository: UserRepository) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return BroadcastViewModel(repository) as T
    }
}