package com.teamnusocial.nusocial.data.model

import com.google.firebase.Timestamp

data class TextMessage(
    val messageText: String,
    val timestamp: Timestamp,
    val sender: String
) {
    constructor() : this("", Timestamp(0,0), "")
}