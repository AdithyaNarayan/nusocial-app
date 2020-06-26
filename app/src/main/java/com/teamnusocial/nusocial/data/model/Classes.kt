package com.teamnusocial.nusocial.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Classes(private val id: String): Parcelable{
    constructor() : this("")
}