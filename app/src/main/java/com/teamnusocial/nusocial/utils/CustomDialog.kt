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

class CustomDialog(val context_: Context, val oldString: String, val commentID: String, val postID: String, val commID: String): Dialog(context_) {
    private lateinit var confirm_button: Button
    private lateinit var cancel_button: Button
    lateinit var input_text: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.custom_dialog)
        findViewById<TextView>(R.id.dialog_title).text = "Edit Comment"
        confirm_button = findViewById(R.id.confirm_edit_comment_button)
        cancel_button = findViewById(R.id.cancel_edit_comment_button)
        input_text = findViewById(R.id.edit_comment_input)
        input_text.setText(oldString)
        confirm_button.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                SocialToolsRepository(FirestoreUtils()).editComment(input_text.text.toString(), commentID, postID, commID)
                withContext(Dispatchers.Main) {
                    dismiss()
                }
            }
        }
        cancel_button.setOnClickListener {
            dismiss()
        }
    }

}