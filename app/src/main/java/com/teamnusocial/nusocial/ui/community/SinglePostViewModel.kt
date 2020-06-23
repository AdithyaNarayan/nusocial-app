package com.teamnusocial.nusocial.ui.community

import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.teamnusocial.nusocial.data.model.Comment
import com.teamnusocial.nusocial.data.model.User

class SinglePostViewModel() : ViewModel() {
    var you: User = User()
    var owner: User = User()
}