package com.teamnusocial.nusocial.data.model

import android.os.Parcelable
import android.util.Log
import com.squareup.okhttp.Dispatcher
import com.teamnusocial.nusocial.utils.FirestoreUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
enum class Gender {
    MALE, FEMALE, OTHERS
}
data class User(
    public var uid: String,
    public val name: String,
    public val gender: Gender,
    public val profilePicturePath: String,
    public val modules: MutableList<Module>,
    public val yearOfStudy: Int,
    public val courseOfStudy: String,
    public val location: LocationLatLng,
    public val buddies: MutableList<String>, // store uid of buddies
    public val about: String,
    public val seenAndMatch: MutableList<String>
) {
    constructor() : this(
        "",
        "",
        Gender.MALE,
        "https://i7.pngflow.com/pngimage/455/105/png-anonymity-computer-icons-anonymous-user-anonymous-purple-violet-logo-smiley-clipart.png",
        mutableListOf(),
        1,
        "",
        LocationLatLng(0.0, 0.0),
        mutableListOf(),
        "",
        mutableListOf()
    )
}