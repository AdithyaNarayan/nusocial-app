package com.teamnusocial.nusocial.data.model

import com.google.firebase.Timestamp

data class TextMessage(
    val messageText: String,
    val timestamp: Timestamp,
    val sender: String,
    val senderName: String,
    val receiver: String
) {
    constructor() : this("", Timestamp(0, 0), "", "", "")
}