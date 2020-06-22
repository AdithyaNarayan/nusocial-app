package com.teamnusocial.nusocial.ui.you

import androidx.lifecycle.ViewModel
import com.teamnusocial.nusocial.data.model.Community

import com.teamnusocial.nusocial.data.model.User

class YouViewModel : ViewModel() {
    var you: User = User()
    var allCommunitites: MutableList<Community> = mutableListOf()
    var allYourCommunities: MutableList<Community> = mutableListOf()
}

