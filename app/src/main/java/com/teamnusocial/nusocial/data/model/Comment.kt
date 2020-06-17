package com.teamnusocial.nusocial.data.model

data class Comment(
  var ownerUid: String,
  var ofPost: String,
  var textContent: String,
  var imageList: MutableList<String>
) {
  constructor(): this(
    "",
    "",
    "--blank--",
    mutableListOf()
  )
}