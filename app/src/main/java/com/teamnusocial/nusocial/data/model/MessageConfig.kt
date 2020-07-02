package com.teamnusocial.nusocial.data.model

data class MessageConfig(var recipients: MutableList<MyPair>, val invisible: String, val name: String) {
    constructor() : this(mutableListOf(), "", "")
}