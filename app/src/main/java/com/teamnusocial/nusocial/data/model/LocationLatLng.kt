package com.teamnusocial.nusocial.data.model

import com.google.android.gms.maps.model.LatLng


data class LocationLatLng(val latitude: Double, val longitude: Double) {
    constructor() : this(0.0, 0.0)

    fun getAsLatLng() = LatLng(latitude, longitude)
}