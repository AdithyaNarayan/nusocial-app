
package com.teamnusocial.nusocial.data.model

import android.os.Parcelable
import com.google.type.LatLng
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue


enum class Gender {
    MALE, FEMALE, OTHERS
}
@Parcelize
data class User(
    public val name: String,
    public val gender: Gender,
    public val modules: @RawValue List<Module>,
    public val yearOfStudy: Int,
    public val courseOfStudy: String,
    public val location: @RawValue LocationLatLng,
    public val buddies: List<String>, // store uid of buddies
    public val about: String
): Parcelable {
    constructor() : this(
        "n0b0n",
        Gender.MALE,
        arrayListOf(),
        1,
        "CS",
        LocationLatLng(0.0, 0.0),
        arrayListOf(),
        "hello"
    )

}