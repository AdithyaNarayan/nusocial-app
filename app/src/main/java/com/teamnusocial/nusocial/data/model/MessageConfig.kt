package com.teamnusocial.nusocial.data.model

import com.google.firebase.Timestamp

data class MessageConfig(
    var recipients: MutableList<MyPair>,
    val invisible: String,
    val name: String,
    val latestMessage: String,
    val latestTime: Timestamp,
    val id: String
) {
    constructor() : this(mutableListOf(), "", "", "", Timestamp.now(), "")
}