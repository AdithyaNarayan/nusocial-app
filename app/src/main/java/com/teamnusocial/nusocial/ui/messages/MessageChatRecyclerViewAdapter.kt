package com.teamnusocial.nusocial.ui.messages

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.teamnusocial.nusocial.data.model.TextMessage
import com.teamnusocial.nusocial.data.repository.UserRepository
import com.teamnusocial.nusocial.utils.FirebaseAuthUtils
import com.teamnusocial.nusocial.utils.FirestoreUtils
import com.teamnusocial.nusocial.utils.getTimeAgo
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MessageChatRecyclerViewAdapter(
    val context: Context,
    options: FirestoreRecyclerOptions<TextMessage>
) : FirestoreRecyclerAdapter<TextMessage, MessageHolder>(options) {
    companion object {
        const val MESSAGE_IN_VIEW_TYPE = 1
        const val MESSAGE_OUT_VIEW_TYPE = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageHolder {
        val view: View = if (viewType == MESSAGE_IN_VIEW_TYPE) {
            LayoutInflater.from(parent.context)
                .inflate(R.layout.messages_received, parent, false)
        } else {
            LayoutInflater.from(parent.context)
                .inflate(R.layout.messages_sent, parent, false)
        }
        return MessageHolder(view)
    }

    override fun onBindViewHolder(
        holder: MessageHolder,
        position: Int,
        model: TextMessage
    ) {
        holder.messageText.text = model.messageText
        if (model.sender == FirestoreUtils().getCurrentUser()!!.uid) {
            holder.sender.text = context.getString(R.string.you)
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                FirestoreUtils().getUserAsDocument(model.sender).get().addOnCompleteListener {
                    holder.sender.text = it.result!!["name"] as String
                }
            }
        }
        holder.timestamp.text = getTimeAgo(model.timestamp.seconds)
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).sender == FirebaseAuthUtils().getCurrentUser()!!.uid)
            MESSAGE_OUT_VIEW_TYPE else MESSAGE_IN_VIEW_TYPE
    }


}