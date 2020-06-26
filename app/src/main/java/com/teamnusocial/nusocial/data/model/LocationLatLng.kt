package com.teamnusocial.nusocial.data.model

import android.location.Location
import android.location.LocationManager
import android.os.Parcelable
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LocationLatLng(val latitude: Double, val longitude: Double, var cluster: String) : Parcelable{
    constructor() : this(0.0, 0.0, "")

    fun getAsLatLng() = LatLng(latitude, longitude)

    fun distanceTo(locationLatLng: LocationLatLng): Double {
        val firstLoc =
            Location(LocationManager.GPS_PROVIDER)
        val secondLoc =
            Location(LocationManager.GPS_PROVIDER)

        firstLoc.latitude = latitude
        firstLoc.longitude = longitude

        secondLoc.latitude = locationLatLng.latitude
        secondLoc.longitude = locationLatLng.longitude
        Log.d("BROADCAST", firstLoc.toString())
        return firstLoc.distanceTo(secondLoc).toDouble()
    }

}