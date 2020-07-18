package com.teamnusocial.nusocial.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.teamnusocial.nusocial.data.repository.AuthUserRepository

@Suppress("UNCHECKED_CAST")
class AuthViewModelFactory(private val repository: AuthUserRepository) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AuthViewModel(repository) as T
    }
}