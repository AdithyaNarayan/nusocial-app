package com.teamnusocial.nusocial.data.model

import com.google.firebase.Timestamp
enum class MessageType {
    TEXT, IMAGE, FILE, SYSTEM
}
data class TextMessage(
    val messageText: String,
    val timestamp: Timestamp,
    val sender: String,
    val senderName: String,
    val receiver: String,
    val messageType: MessageType
) {
    constructor() : this("", Timestamp(0, 0), "", "", "", MessageType.TEXT)

}