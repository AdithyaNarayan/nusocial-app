package com.teamnusocial.nusocial.ui.you

import androidx.lifecycle.ViewModel
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.teamnusocial.nusocial.data.model.Community
import com.teamnusocial.nusocial.data.model.Module

import com.teamnusocial.nusocial.data.model.User
import kotlinx.coroutines.coroutineScope
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class YouViewModel : ViewModel() {
    var you: User = User()
    var allCommunitites: MutableList<Community> = mutableListOf()
    var allYourCommunities: MutableList<Community> = mutableListOf()
    var moduleCommunities: MutableList<Community> = mutableListOf()
    var otherCommunities: MutableList<Community> = mutableListOf()
    var allModulesAvailable: List<Module> = listOf()
    val service = Retrofit.Builder()
        .baseUrl("https://api.nusmods.com/v2/")
        .addConverterFactory(MoshiConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()
        .create(AllModulesAvailable::class.java)

    suspend fun allModulesOffered() = coroutineScope {
        val modules = service.retrieveModules("2019-2020").await()
        modules
    }

}

