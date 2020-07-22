package com.teamnusocial.nusocial.ui.broadcast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.teamnusocial.nusocial.data.repository.UserRepository
import com.teamnusocial.nusocial.ui.buddymatch.BuddyMatchViewModel

@Suppress("UNCHECKED_CAST")
class BuddyMatchViewModelFactory(private val repository: UserRepository) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return BuddyMatchViewModel(repository) as T
    }
}