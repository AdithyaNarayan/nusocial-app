package com.teamnusocial.nusocial.ui.community

import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.teamnusocial.nusocial.data.model.Comment

class SinglePostViewModel() : ViewModel() {
    var allComments: MutableList<Comment> = mutableListOf(Comment("","123","Hieu",
        Timestamp(0,0),"","TEST", mutableListOf()))
}