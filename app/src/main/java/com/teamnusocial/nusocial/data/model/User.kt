package com.teamnusocial.nusocial.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

enum class Gender {
    MALE, FEMALE, OTHERS
}

@Parcelize
data class User(
    val name: String,
    val gender: Gender,
    val profilePicturePath: String,
    val modules: @RawValue List<Module>,
    val yearOfStudy: Int,
    val courseOfStudy: String,
    val location: @RawValue LocationLatLng,
    val buddies: List<String>, // store uid of buddies
    val about: String
) : Parcelable {
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