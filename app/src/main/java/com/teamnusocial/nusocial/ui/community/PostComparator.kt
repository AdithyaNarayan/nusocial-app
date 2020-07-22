package com.teamnusocial.nusocial.ui.community

import com.google.firebase.Timestamp
import com.teamnusocial.nusocial.data.model.Post

class PostComparator {
    companion object : Comparator<Post> {
        override fun compare(a: Post, b: Post): Int {
            val aValue = (a.userLikeList.size / (a.timeStamp.seconds - Timestamp.now().seconds)) * 60
            val bValue = (b.userLikeList.size / (b.timeStamp.seconds - Timestamp.now().seconds))*60
            if(aValue < bValue) return 1
            else if(bValue > aValue) return -1
            else return 0
        }
    }
}