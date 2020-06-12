package com.teamnusocial.nusocial.data.model

data class MessageConfig(val recipients: List<Pair<String, String>>, val invisible: String) {
    constructor() : this(listOf(), "")
}