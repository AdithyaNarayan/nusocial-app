package com.teamnusocial.nusocial.ui.messages

import com.teamnusocial.nusocial.R
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class MessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var sender: TextView = itemView.findViewById(R.id.messageSender)
    var timestamp: TextView = itemView.findViewById(R.id.time)
    val messageText: TextView = itemView.findViewById(R.id.messageText)

}