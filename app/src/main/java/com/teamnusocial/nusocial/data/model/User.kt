package com.teamnusocial.nusocial.data.model

enum class Gender {
    MALE, FEMALE, OTHERS
}

data class User(
    public val name: String,
    public val gender: Gender,
    public val profilePicturePath: String,
    public val modules: List<Module>,
    public val yearOfStudy: Int,
    public val courseOfStudy: String,
    public val location: LocationLatLng,
    public val buddies: List<String>, // store uid of buddies
    public val about: String
) {
    constructor() : this(
        "",
        Gender.MALE,
        "",
        listOf(),
        1,
        "",
        LocationLatLng(0.0, 0.0),
        listOf(),
        ""
    )
}