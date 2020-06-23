
package com.teamnusocial.nusocial.data.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Module(
    val moduleCode: String,
    @field:Json(name = "title")
    val moduleName: String,
    val classes: List<Classes>
): Parcelable {
    constructor() : this("", "", listOf())
}