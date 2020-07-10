package com.teamnusocial.nusocial.data.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize
enum class PostType {
    Poll, Question, Sharing, Discussion, Announcement
}
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
    var parentCommName: String,
    var postType: PostType
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
        "",
        PostType.Question
    )
}