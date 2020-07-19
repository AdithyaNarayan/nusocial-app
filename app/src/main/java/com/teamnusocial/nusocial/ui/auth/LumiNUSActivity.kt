package com.teamnusocial.nusocial.ui.auth

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.teamnusocial.nusocial.HomeActivity
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.repository.AuthUserRepository
import com.teamnusocial.nusocial.utils.FirebaseAuthUtils
import kotlinx.android.synthetic.main.activity_lumi_n_u_s.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class LumiNUSActivity : AppCompatActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lumi_n_u_s)

        luminusWebView.webViewClient = MyWebViewClient(this)
        luminusWebView.loadUrl("https://vafs.nus.edu.sg/adfs/oauth2/authorize?response_type=code&client_id=INC000002163230&resource=sg_edu_nus_oauth&redirect_uri=https%3A%2F%2Ffirestore.googleapis.com%2Fv1%2Fprojects%2Fnusocial-7c7e8%2Fdatabases%2F%28default%29%2Fdocuments%2Fusers%2F")

        luminusWebView.settings.javaScriptEnabled = true
        luminusWebView.settings.loadWithOverviewMode = true
        luminusWebView.settings.useWideViewPort = true
        luminusWebView.settings.domStorageEnabled = true
    }

    inner class MyWebViewClient internal constructor(private val activity: Activity) :
        WebViewClient() {

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            val url: String = request?.url.toString();
            view?.loadUrl(url)
            if (url.contains("?code=")) {
                Log.d("AUTH", url)
                Log.d("AUTH", url.substring(url.indexOf("?code=") + 6))
                val code = url.substring(url.indexOf("?code=") + 6)
                val repository = AuthUserRepository(FirebaseAuthUtils())
                luminusWebView.loadUrl("https://nusocial-bridge-api.herokuapp.com/loading")
                CoroutineScope(Dispatchers.IO).launch {
                    val accessToken = repository.getAccessToken(code)
                    val user = repository.getUserFromToken(accessToken)
                    val userID = user.uid

                    val firebaseToken = repository.getFirebaseToken(userID)

                    FirebaseAuth.getInstance().signInWithCustomToken(firebaseToken)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                val docRef = FirebaseFirestore.getInstance().collection("users")
                                    .document(userID)
                                docRef.get().addOnCompleteListener {snapshot ->
                                    if (snapshot.isSuccessful) {
                                        if (snapshot.result!!["id"] == null) {
                                            docRef.set(user)
                                        }
                                    } else {
                                        docRef.set(user)
                                    }

                                    startActivity(
                                        Intent(
                                            this@LumiNUSActivity,
                                            HomeActivity::class.java
                                        )
                                    )
                                }

                            } else {
                                Log.d("AUTH", it.exception.toString())
                            }
                        }
                }
            }
            return true
        }

        override fun shouldOverrideUrlLoading(webView: WebView, url: String): Boolean {
            webView.loadUrl(url)
            return true
        }

        override fun onReceivedError(
            view: WebView,
            request: WebResourceRequest,
            error: WebResourceError
        ) {
            Toast.makeText(activity, "Got Error! $error", Toast.LENGTH_SHORT).show()
        }
    }
}