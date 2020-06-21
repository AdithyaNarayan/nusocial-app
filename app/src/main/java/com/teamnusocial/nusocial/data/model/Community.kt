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
) : Parcelable