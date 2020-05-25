package com.teamnusocial.nusocial.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.utils.FirebaseAuthUtils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        uid.text = FirebaseAuthUtils().getCurrentUser()!!.email.toString()
    }
}