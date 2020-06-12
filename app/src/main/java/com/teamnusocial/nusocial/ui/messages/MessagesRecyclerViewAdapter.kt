package com.teamnusocial.nusocial.ui.messages

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.teamnusocial.nusocial.R


class MessagesRecyclerViewAdapter(
    val context: Context?,
    val data: List<Pair<String, String>>
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
        val userName = data[position].second
        holder.userNameTextView.text = userName
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var userNameTextView: TextView = itemView.findViewById(R.id.messageUserName)
        override fun onClick(view: View?) {
            clickListener.onItemClick(view, adapterPosition)
        }

        init {
            itemView.setOnClickListener(this)
        }
    }

    fun getItem(id: Int): String {
        return data[id].second
    }

    fun setClickListener(itemClickListener: ItemClickListener) {
        clickListener = itemClickListener
    }

    interface ItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }

}