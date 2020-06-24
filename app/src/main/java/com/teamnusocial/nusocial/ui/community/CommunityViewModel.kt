package com.teamnusocial.nusocial.ui.community

import androidx.lifecycle.ViewModel
import com.teamnusocial.nusocial.data.model.Post
import com.teamnusocial.nusocial.data.model.User

class CommunityViewModel : ViewModel() {
    var you: User = User()
    var allPosts: MutableList<Post> = mutableListOf()
    //var allSingleCommPost: MutableList<Post> = mutableListOf()
}