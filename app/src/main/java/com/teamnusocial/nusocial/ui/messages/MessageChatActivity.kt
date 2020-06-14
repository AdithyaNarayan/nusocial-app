package com.teamnusocial.nusocial.ui.messages

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.model.TextMessage
import com.teamnusocial.nusocial.data.model.User
import com.teamnusocial.nusocial.data.repository.UserRepository
import com.teamnusocial.nusocial.utils.FirestoreUtils
import kotlinx.android.synthetic.main.activity_message_chat.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MessageChatActivity : AppCompatActivity() {
    private lateinit var adapter: FirestoreRecyclerAdapter<TextMessage, MessageHolder>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_chat)
        val messageID = intent.getStringExtra("messageID")
        val messageName = intent.getStringExtra("messageName")
        topBarChat.title = messageName

        val firestoreUtils = FirestoreUtils()
        sendMessageButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                UserRepository(FirestoreUtils()).sendMessage(
                    messageID!!,
                    sendMessageEditText.text.toString()
                )
            }
            sendMessageEditText.setText("")
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
}