package com.teamnusocial.nusocial.data.model

import com.google.firebase.Timestamp

data class Post(
    var uid: String,
    var ownerUid: String,
    var textContent: String,
    var imageList: MutableList<String>,
    var timeStamp: Timestamp,
    var isLike: Boolean,
    var numLike: Int,
    var numComment: Int,
    var commentList: MutableList<Comment>
) {
    constructor() : this(
        "",
        "",
        "--blank--",
        mutableListOf(),
        Timestamp(0,0),
        false,
        0,
        0,
        mutableListOf()
    )
}