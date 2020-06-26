package com.teamnusocial.nusocial.data.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Comment(
  var id: String,
  var ownerUid: String,
  var ownerName: String,
  var ownerProfileImageUrl: String,
  var timeStamp: Timestamp,
  var parentPostID: String,
  var parentCommunityID: String,
  var textContent: String,
  var imageList: MutableList<String>
): Parcelable {
  constructor(): this(
    "",
    "",
    "https://image.flaticon.com/icons/png/512/64/64572.png",
    "--Blank--",
    Timestamp(0,0),
    "",
    "",
    "--blank--",
    mutableListOf()
  )
}