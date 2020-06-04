package com.teamnusocial.nusocial.ui.buddymatch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.squareup.picasso.Picasso

class BuddyMatchViewModel : ViewModel() {
    private val imageURL = MutableLiveData<ArrayList<String>>().apply {
        val url = "https://scontent-xsp1-1.xx.fbcdn.net/v/t1.0-9/s960x960/78794853_2434835663499592_1312362149607112704_o.jpg?_nc_cat=110&_nc_sid=85a577&_nc_ohc=RV9gAt1ZQpkAX9xi11o&_nc_ht=scontent-xsp1-1.xx&_nc_tp=7&oh=881e036b42df80a44db5b16e4f50286f&oe=5EFC8264"
        value = arrayListOf(url)
    }
    val images: LiveData<ArrayList<String>> = imageURL
}