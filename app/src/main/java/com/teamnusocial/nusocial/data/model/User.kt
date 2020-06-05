package com.teamnusocial.nusocial.data.model

import com.google.android.gms.maps.model.LatLng

enum class Gender {
    MALE, FEMALE, OTHERS
}

data class User(
    public val name: String,
    public val gender: Gender,
    public val modules: List<Module>,
    public val yearOfStudy: Int,
    public val courseOfStudy: String,
    public val location: LatLng,
    public val buddies: List<String>, // store uid of buddies
    public val about: String
) {
    constructor() : this("", Gender.MALE, listOf(), 1, "", LatLng(0.0, 0.0), listOf(), "")
}