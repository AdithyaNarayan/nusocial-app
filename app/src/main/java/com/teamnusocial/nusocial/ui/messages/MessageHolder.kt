package com.teamnusocial.nusocial.ui.messages

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.teamnusocial.nusocial.R
import soup.neumorphism.NeumorphFloatingActionButton


class MessageHolder(itemView: View, viewType: Int) : RecyclerView.ViewHolder(itemView) {

    lateinit var sender: ImageView
    lateinit var timestamp: TextView
    lateinit var messageText: TextView
    lateinit var messageImage: ImageView
    lateinit var downloadFileButton: NeumorphFloatingActionButton

    companion object {
        const val MESSAGE_IN_TEXT_VIEW_TYPE = 1
        const val MESSAGE_OUT_TEXT_VIEW_TYPE = 2
        const val MESSAGE_IN_IMAGE_VIEW_TYPE = 3
        const val MESSAGE_OUT_IMAGE_VIEW_TYPE = 4
        const val MESSAGE_IN_FILE_VIEW_TYPE = 5
        const val MESSAGE_OUT_FILE_VIEW_TYPE = 6
        const val MESSAGE_SYSTEM_VIEW_TYPE = 7
    }

    init {
        when (viewType) {
            MESSAGE_IN_TEXT_VIEW_TYPE, MESSAGE_OUT_TEXT_VIEW_TYPE -> {
                sender = itemView.findViewById(R.id.messageSender)
                timestamp = itemView.findViewById(R.id.time)
                messageText = itemView.findViewById(R.id.messageText)
            }

            MESSAGE_IN_IMAGE_VIEW_TYPE, MESSAGE_OUT_IMAGE_VIEW_TYPE -> {
                sender = itemView.findViewById(R.id.messageSender)
                timestamp = itemView.findViewById(R.id.time)
                messageImage = itemView.findViewById(R.id.messageImage)
            }

            MESSAGE_IN_FILE_VIEW_TYPE, MESSAGE_OUT_FILE_VIEW_TYPE -> {
                sender = itemView.findViewById(R.id.messageSender)
                timestamp = itemView.findViewById(R.id.time)
                messageText = itemView.findViewById(R.id.messageText)
                downloadFileButton = itemView.findViewById(R.id.downloadFileButton)
            }

            MESSAGE_SYSTEM_VIEW_TYPE -> {
                messageText = itemView.findViewById(R.id.messageText)
            }
        }
    }
}