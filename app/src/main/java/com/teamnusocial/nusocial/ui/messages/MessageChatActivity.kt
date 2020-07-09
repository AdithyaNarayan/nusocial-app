package com.teamnusocial.nusocial.ui.messages

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.model.MessageType
import com.teamnusocial.nusocial.data.model.TextMessage
import com.teamnusocial.nusocial.data.repository.UserRepository
import com.teamnusocial.nusocial.utils.FirestoreUtils
import kotlinx.android.synthetic.main.activity_message_chat.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class MessageChatActivity : AppCompatActivity() {
    companion object {
        private const val PICK_IMAGE_REQUEST = 71
        private const val PICK_FILE_REQUEST = 72
    }
    private lateinit var adapter: FirestoreRecyclerAdapter<TextMessage, MessageHolder>
    private lateinit var filePath: Uri
    private lateinit var messageID: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_chat)
        messageID = intent.getStringExtra("messageID")!!
        val messageName = intent.getStringExtra("messageName")
        topBarChat.title = messageName

        val firestoreUtils = FirestoreUtils()
        sendMessageButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                UserRepository(FirestoreUtils()).sendMessage(
                    messageID,
                    sendMessageEditText.text.toString(),
                    MessageType.TEXT
                )
            }
            sendMessageEditText.setText("")
        }
        sendImageButton.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Select Picture"),
                PICK_IMAGE_REQUEST
            )
        }
        sendFileButton.setOnClickListener {
            val intent = Intent()
            intent.type = "*/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Select File"),
                PICK_FILE_REQUEST
            )
        }

        val query =
            firestoreUtils.getMessages(messageID!!).collection("messages")
                .orderBy("timestamp", Query.Direction.DESCENDING)
        adapter = MessageChatRecyclerViewAdapter(
            this, FirestoreRecyclerOptions.Builder<TextMessage>()
                .setQuery(query, TextMessage::class.java)
                .build()
        )
        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        messageChatRecyclerView.layoutManager = layoutManager
        messageChatRecyclerView.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        if (this::adapter.isInitialized) adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        if (this::adapter.isInitialized) adapter.stopListening()
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null
        ) {
            val storageReference = FirebaseStorage.getInstance().reference
            val ref = storageReference.child("message_images/$messageID/${UUID.randomUUID()}")
            ref.putFile(data.data!!).addOnCompleteListener {
                if (it.isSuccessful) {
                    ref.downloadUrl.addOnSuccessListener {
                        CoroutineScope(Dispatchers.IO).launch {
                            UserRepository(FirestoreUtils()).sendMessage(
                                messageID,
                                it.toString(),
                                MessageType.IMAGE
                            )
                        }
                    }
                }
            }
        }
        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null
        ) {
            val storageReference = FirebaseStorage.getInstance().reference
            val ref = storageReference.child("message_files/$messageID/${UUID.randomUUID()}")
            ref.putFile(data.data!!).addOnCompleteListener {
                if (it.isSuccessful) {
                    ref.downloadUrl.addOnSuccessListener {
                        CoroutineScope(Dispatchers.IO).launch {
                            UserRepository(FirestoreUtils()).sendMessage(
                                messageID,
                                it.toString(),
                                MessageType.FILE
                            )
                        }
                    }
                }
            }
        }
    }
}