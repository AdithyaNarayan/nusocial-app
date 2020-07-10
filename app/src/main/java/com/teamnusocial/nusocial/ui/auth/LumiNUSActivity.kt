package com.teamnusocial.nusocial.ui.auth

import android.annotation.SuppressLint
import android.app.Activity
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
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.repository.AuthUserRepository
import com.teamnusocial.nusocial.utils.FirebaseAuthUtils
import kotlinx.android.synthetic.main.activity_lumi_n_u_s.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.http.client.methods.HttpPost


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
                CoroutineScope(Dispatchers.IO).launch {
                    val accessToken = AuthUserRepository(FirebaseAuthUtils()).getAccessToken(code)
                    Log.d("AUTH", accessToken)
                }
//                CoroutineScope(Dispatchers.IO).launch {
//
//                    val httpclient = HttpClients.createDefault()
//                    val uri =
//                        URIBuilder(
//                            "https://luminus.azure-api.net/login/ADFSToken"
////                                    "?client_id=INC000002163230" +
////                                    "&code=${code}" +
////                                    "&redirect_uri=https%3A%2F%2Ffirestore.googleapis.com%2Fv1%2Fprojects%2Fnusocial-7c7e8%2Fdatabases%2F%28default%29%2Fdocuments%2Fusers%2F" +
////                                    "&grant_type=authorization_code" +
////                                    "&resource=sg_edu_nus_oauth"
//                        ).build()
//                    var urlRequest = "https://luminus.azure-api.net/login/ADFSToken?"
//                    urlRequest += URLEncoder.encode("client_id", "UTF-8") + "=" + URLEncoder.encode("INC000002163230", "UTF-8")
//                    urlRequest += "&" + URLEncoder.encode("code", "UTF-8") + "=" + URLEncoder.encode(code, "UTF-8")
//                    urlRequest += "&" + URLEncoder.encode("redirect_uri", "UTF-8") + "=" + URLEncoder.encode("https://firestore.googleapis.com/v1/projects/nusocial-7c7e8/databases/(default)/documents/users/", "UTF-8")
//                    urlRequest += "&" + URLEncoder.encode("grant_type", "UTF-8") + "=" + URLEncoder.encode("authorization_code", "UTF-8")
//                    urlRequest += "&" + URLEncoder.encode("resource", "UTF-8") + "=" + URLEncoder.encode("sg_edu_nus_oauth", "UTF-8")
//
//                    Log.d("AUTH", urlRequest)
//                    val requestToken =
//                        HttpPost(URIBuilder(urlRequest).build())
//                    val reqEntity =
//                        StringEntity("{body}")
//                    requestToken.setHeader(
//                        "Ocp-Apim-Subscription-Key",
//                        "c9672e39d6854ec084706e9a944f8b21"
//                    )
//                    requestToken.setHeader(
//                        "Content-Type",
//                        "application/x-www-form-urlencoded"
//                    )
//
//                    requestToken.entity = reqEntity
//                    val response: HttpResponse = httpclient.execute(requestToken)
//                    val entity = response.entity
//                    Log.d("AUTH", requestToken.uri.toString())
//                    Log.d("AUTH", EntityUtilsHC4.toString(entity))
//                }
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