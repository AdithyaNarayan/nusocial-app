package com.teamnusocial.nusocial.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Community(
    public var id: String,
    public var name: String,
    public var allPostsID: MutableList<String>,
    public var allMembersID: MutableList<String>,
    public var module: Module,
    public var coverImageUrl: String,
    public var allAdminsID: MutableList<String>
) : Parcelable {
    constructor(): this(
        "",
        "--blank--",
        mutableListOf(),
        mutableListOf(),
        Module(),
        "https://lh3.googleusercontent.com/proxy/Xdn8e0QgBdV7nl1Vj8u60dPAEM5xZjZ2R4S6e21W4DZM3mOUHBBm5L2MIW4W5iu0rxRrDaDrb3l-O_5C9zp_mP6IowDRmAkKhe7vpFA3NHb3UjuGdzrb",
        mutableListOf()
    )
}