package com.teamnusocial.nusocial.data.model

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("status_code")
    var statusCode: Int,
    @SerializedName("auth_token")
    var authToken: String
) {

}