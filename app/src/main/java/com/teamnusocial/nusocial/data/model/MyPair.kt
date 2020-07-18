package com.teamnusocial.nusocial.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class MyPair(val first: String, val second: String): Parcelable {
    constructor(): this("", "")
}
