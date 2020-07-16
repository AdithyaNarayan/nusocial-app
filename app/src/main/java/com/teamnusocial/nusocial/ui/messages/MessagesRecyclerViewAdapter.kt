package com.teamnusocial.nusocial.ui.messages

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.model.MessageConfig
import com.teamnusocial.nusocial.data.repository.UserRepository
import com.teamnusocial.nusocial.utils.FirestoreUtils
import com.teamnusocial.nusocial.utils.getTimeAgo
import jp.wasabeef.picasso.transformations.MaskTransformation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import soup.neumorphism.NeumorphCardView


class MessagesRecyclerViewAdapter(
    val context: Context?,
    val data: List<MessageConfig>
) :
    RecyclerView.Adapter<MessagesRecyclerViewAdapter.ViewHolder>() {
    private val inflater = LayoutInflater.from(context)
    private lateinit var clickListener: ItemClickListener

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view: View = inflater.inflate(R.layout.item_message_user, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: MessagesRecyclerViewAdapter.ViewHolder,
        position: Int
    ) {
        holder.messageCardView.setOnClickListener {
            val intent = Intent(context, MessageChatActivity::class.java)
            intent.putExtra("messageID", data[position].id)
            if (data[position].recipients.size < 2) {
                intent.putExtra("messageName", data[position].recipients[0].second)
            } else {
                intent.putExtra("messageName", data[position].name)
            }
            context?.startActivity(intent)
        }
        holder.userNameTextView.text = if (data[position].recipients.size >= 2) {
            data[position].name
        } else {
            data[position].recipients[0].second
        }
        if (data[position].recipients.size >= 2) {
            val url =
                "https://img.pngio.com/group-icon-png-15-clip-arts-for-free-download-on-een-2019-group-icon-png-512_511.png"
            Picasso
                .get()
                .load(url)
                /*.transform(
                    MaskTransformation(
                        context,
                        R.drawable.round_rect_transformation
                    )
                )*/
                .into(holder.profilePictureImageView)
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                UserRepository(FirestoreUtils()).getUserAnd(data[position].recipients[0].first) {
                    Picasso
                        .get()
                        .load(it.profilePicturePath)
                       // .resize(56, 56)
                       /* .transform(
                            MaskTransformation(
                                context,
                                R.drawable.round_rect_transformation
                            )
                        )*/
                        .into(holder.profilePictureImageView)
                }
            }
        }
        holder.timestampTextView.text = getTimeAgo(data[position].latestTime.seconds)
        holder.latestMessageTextView.text = data[position].latestMessage


    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        val userNameTextView: TextView = itemView.findViewById(R.id.messageUserName)
        val profilePictureImageView: ImageView = itemView.findViewById(R.id.profilePictureView)
        val timestampTextView: TextView = itemView.findViewById(R.id.time_stamp_msg)
        val latestMessageTextView: TextView = itemView.findViewById(R.id.latest_msg)
        val messageCardView: NeumorphCardView = itemView.findViewById(R.id.messageCard)

        override fun onClick(view: View?) {
            clickListener.onItemClick(view, adapterPosition)
            clickListener.onItemClick(itemView, adapterPosition)
        }

        init {
            itemView.setOnClickListener(this)
        }
    }

    fun setClickListener(itemClickListener: ItemClickListener) {
        clickListener = itemClickListener
    }

    interface ItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }

}