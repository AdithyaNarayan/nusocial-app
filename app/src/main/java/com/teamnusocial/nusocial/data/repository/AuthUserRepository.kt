package com.teamnusocial.nusocial.data.repository

import com.teamnusocial.nusocial.data.model.User
import com.teamnusocial.nusocial.utils.FirebaseAuthUtils
import org.apache.http.NameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.HttpClients
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtilsHC4
import org.json.JSONObject

class AuthUserRepository(private val authUtils: FirebaseAuthUtils) {

    fun isSignedIn() = authUtils.getCurrentUser() != null

    fun createUser(email: String, password: String) = authUtils.createUser(email, password)

    fun signInUser(email: String, password: String) = authUtils.signInUser(email, password)

    fun forgotPassword(email: String) = authUtils.forgotPassword(email)

    suspend fun initializeUser(userID: String, name: String) =
        authUtils.initializeUser(userID, name)


    fun getAccessToken(code: String): String {
        val httpClient = HttpClients.createDefault()
        val post = HttpPost("https://luminus.azure-api.net/login/ADFSToken")

        val params = arrayListOf<NameValuePair>()
        params.add(BasicNameValuePair("client_id", "INC000002163230"))
        params.add(BasicNameValuePair("code", code))
        params.add(
            BasicNameValuePair(
                "redirect_uri",
                "https://firestore.googleapis.com/v1/projects/nusocial-7c7e8/databases/(default)/documents/users/"
            )
        )
        params.add(BasicNameValuePair("grant_type", "authorization_code"))
        params.add(BasicNameValuePair("resource", "sg_edu_nus_oauth"))

        post.setHeader(
            "Ocp-Apim-Subscription-Key",
            "c9672e39d6854ec084706e9a944f8b21"
        )
        post.setHeader(
            "Content-Type",
            "application/x-www-form-urlencoded"
        )
        post.entity = UrlEncodedFormEntity(params, "UTF-8")
        val string = EntityUtilsHC4.toString(httpClient.execute(post).entity)
        return JSONObject(string)["access_token"] as String
    }

    fun getUserFromToken(token: String): User {
        val httpClient = HttpClients.createDefault()
        val request = HttpGet("https://luminus.azure-api.net/user/Profile")

        request.setHeader(
            "Ocp-Apim-Subscription-Key",
            "c9672e39d6854ec084706e9a944f8b21"
        )
        request.setHeader("Authorization", "Bearer $token")
        request.setHeader(
            "Content-Type",
            "application/x-www-form-urlencoded"
        )
        val response = JSONObject(EntityUtilsHC4.toString(httpClient.execute(request).entity))
        val user = User()
        user.name = response["userNameOriginal"] as String
        user.courseOfStudy = response["userFaculty"] as String
        user.uid = response["userID"] as String
        return user
    }

    fun getFirebaseToken(uid: String): String {
        val httpClient = HttpClients.createDefault()
        val request = HttpPost("http://nusocial-bridge-api.herokuapp.com/firebaseToken?id=${uid}")

        request.setHeader(
            "Content-Type",
            "application/x-www-form-urlencoded"
        )
        return EntityUtilsHC4.toString(httpClient.execute(request).entity)
    }
}