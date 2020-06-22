package com.teamnusocial.nusocial.data.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Post(
    var id: String,
    var communityID: String,
    var ownerUid: String,
    var textContent: String,
    var imageList: MutableList<String>,
    var videoList: MutableList<String>,
    var timeStamp: Timestamp,
    var userLikeList: MutableList<String>,
    var commentList: MutableList<String>
): Parcelable {
    constructor() : this(
        "",
        "",
        "",
        "--blank--",
        mutableListOf(),
        mutableListOf(),
        Timestamp(0,0),
        mutableListOf(),
        mutableListOf()
    )
}