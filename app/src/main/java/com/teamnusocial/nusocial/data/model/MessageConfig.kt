package com.teamnusocial.nusocial.data.model

data class MessageConfig(var recipients: MutableList<MyPair>, val invisible: String, val name: String, val id : String) {
    constructor() : this(mutableListOf(), "", "","")
}