package com.teamnusocial.nusocial.utils

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.repository.SocialToolsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.Text

class CustomTextViewDialog(val context_: Context, val content: String, val title: String): Dialog(context_) {
    private lateinit var cancel_button: Button
    private lateinit var text: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.custom_text_view_dialog)

        findViewById<TextView>(R.id.about_comm_title).text = title
        cancel_button = findViewById(R.id.done_about_comm)
        text = findViewById(R.id.about_comm_content)
        text.setText(content)
        cancel_button.setOnClickListener {
            dismiss()
        }
    }
}