package com.teamnusocial.nusocial.data.model

data class Poll(
    var options: MutableList<Pair<String, Int>>
) {
  constructor() : this(
      mutableListOf()
  )
}