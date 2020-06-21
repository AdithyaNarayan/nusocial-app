
package com.teamnusocial.nusocial.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.time.LocalTime

@Parcelize
data class Module(
    val moduleCode: String,
    val moduleName: String,
    val classes: List<Classes>
): Parcelable {

    constructor() : this("", "", listOf())

}