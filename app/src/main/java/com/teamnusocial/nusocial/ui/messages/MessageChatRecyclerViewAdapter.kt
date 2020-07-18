package com.teamnusocial.nusocial.ui.messages

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.squareup.picasso.Picasso
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.model.MessageType
import com.teamnusocial.nusocial.data.model.TextMessage
import com.teamnusocial.nusocial.data.repository.UserRepository
import com.teamnusocial.nusocial.utils.FirebaseAuthUtils
import com.teamnusocial.nusocial.utils.FirestoreUtils
import com.teamnusocial.nusocial.utils.getTimeAgo
import jp.wasabeef.picasso.transformations.MaskTransformation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MessageChatRecyclerViewAdapter(
    val context: Context,
    options: FirestoreRecyclerOptions<TextMessage>
) : FirestoreRecyclerAdapter<TextMessage, MessageHolder>(options) {

    companion object {
        const val MESSAGE_IN_TEXT_VIEW_TYPE = 1
        const val MESSAGE_OUT_TEXT_VIEW_TYPE = 2
        const val MESSAGE_IN_IMAGE_VIEW_TYPE = 3
        const val MESSAGE_OUT_IMAGE_VIEW_TYPE = 4
        const val MESSAGE_IN_FILE_VIEW_TYPE = 5
        const val MESSAGE_OUT_FILE_VIEW_TYPE = 6
        const val MESSAGE_SYSTEM_VIEW_TYPE = 7
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageHolder {
        val view: View =
            when (viewType) {
                MESSAGE_IN_TEXT_VIEW_TYPE -> LayoutInflater.from(parent.context)
                    .inflate(R.layout.messages_received, parent, false)
                MESSAGE_OUT_TEXT_VIEW_TYPE -> LayoutInflater.from(parent.context)
                    .inflate(R.layout.messages_sent, parent, false)
                MESSAGE_IN_IMAGE_VIEW_TYPE -> LayoutInflater.from(parent.context)
                    .inflate(R.layout.messages_received_image, parent, false)
                MESSAGE_OUT_IMAGE_VIEW_TYPE -> LayoutInflater.from(parent.context)
                    .inflate(R.layout.messages_sent_image, parent, false)
                MESSAGE_IN_FILE_VIEW_TYPE -> LayoutInflater.from(parent.context)
                    .inflate(R.layout.messages_received_file, parent, false)
                MESSAGE_OUT_FILE_VIEW_TYPE -> LayoutInflater.from(parent.context)
                    .inflate(R.layout.messages_sent_file, parent, false)
                MESSAGE_SYSTEM_VIEW_TYPE -> LayoutInflater.from(parent.context)
                    .inflate(R.layout.messages_system, parent, false)
                else -> LayoutInflater.from(parent.context)
                    .inflate(R.layout.messages_system, parent, false)
            }

        return MessageHolder(view, viewType)
    }

    override fun onBindViewHolder(
        holder: MessageHolder,
        position: Int,
        model: TextMessage
    ) {
        when (model.messageType) {
            MessageType.TEXT -> {
                holder.messageText.text = model.messageText
                CoroutineScope(Dispatchers.IO).launch {
                    UserRepository(FirestoreUtils()).getUserAnd(model.sender) {
                        Picasso
                            .get()
                            .load(it.profilePicturePath)
                            .resize(30, 30)
                            .transform(
                                MaskTransformation(
                                    context,
                                    R.drawable.round_rect_transformation
                                )
                            )
                            .into(holder.sender)
                    }
                }
                holder.timestamp.text = getTimeAgo(model.timestamp.seconds)
            }
            MessageType.IMAGE -> {
                    Picasso.get().load(model.messageText).resize(240, 180)
                        .transform(
                            MaskTransformation(
                                context,
                                R.drawable.round_rect_transformation
                            )
                        )
                        .into(holder.messageImage)


                CoroutineScope(Dispatchers.IO).launch {
                    UserRepository(FirestoreUtils()).getUserAnd(model.sender) {
                        Picasso
                            .get()
                            .load(it.profilePicturePath)
                            .resize(30, 30)
                            .transform(
                                MaskTransformation(
                                    context,
                                    R.drawable.round_rect_transformation
                                )
                            )
                            .into(holder.sender)
                    }
                }

                holder.timestamp.text = getTimeAgo(model.timestamp.seconds)
            }
            MessageType.FILE -> {
                val title = getTitle(model.messageText)
                holder.downloadFileButton.setOnClickListener {
                    val request = DownloadManager.Request(Uri.parse(model.messageText))

                    request.setTitle(title)
                    request.setDescription("Downloading file from NUSocial Servers")
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

                    val reference =
                        (context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager).enqueue(
                            request
                        )
                }
                holder.messageText.text = title
                CoroutineScope(Dispatchers.IO).launch {
                    UserRepository(FirestoreUtils()).getUserAnd(model.sender) {
                        Picasso
                            .get()
                            .load(it.profilePicturePath)
                            .resize(30, 30)
                            .transform(
                                MaskTransformation(
                                    context,
                                    R.drawable.round_rect_transformation
                                )
                            )
                            .into(holder.sender)
                    }
                }

                holder.timestamp.text = getTimeAgo(model.timestamp.seconds)
            }
            MessageType.SYSTEM -> {
                holder.messageText.text = model.messageText
            }
        }
    }


    private fun getTitle(url: String): String = "File name"

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position).messageType) {
            MessageType.TEXT -> if (getItem(position).sender == FirebaseAuthUtils().getCurrentUser()!!.uid)
                MESSAGE_OUT_TEXT_VIEW_TYPE else MESSAGE_IN_TEXT_VIEW_TYPE

            MessageType.IMAGE -> if (getItem(position).sender == FirebaseAuthUtils().getCurrentUser()!!.uid)
                MESSAGE_OUT_IMAGE_VIEW_TYPE else MESSAGE_IN_IMAGE_VIEW_TYPE

            MessageType.FILE -> if (getItem(position).sender == FirebaseAuthUtils().getCurrentUser()!!.uid)
                MESSAGE_OUT_FILE_VIEW_TYPE else MESSAGE_IN_FILE_VIEW_TYPE
            MessageType.SYSTEM -> MESSAGE_SYSTEM_VIEW_TYPE
        }
    }


}