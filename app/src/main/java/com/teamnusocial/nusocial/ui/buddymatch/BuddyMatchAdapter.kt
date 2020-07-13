package com.teamnusocial.nusocial.ui.buddymatch

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.model.User

class BuddyMatchAdapter(private val data: List<User>, private val currUser: User) :
    RecyclerView.Adapter<BuddyMatchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuddyMatchViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_buddymatch, parent, false)
        return BuddyMatchViewHolder(parent.context, itemView)
    }

    override fun onBindViewHolder(holder: BuddyMatchViewHolder, position: Int) {
        if (position == 0) {
            holder.mainView.setPadding(172, 0, 0, 0)
        }
        if (position == data.lastIndex) {
            holder.mainView.setPadding(0, 0, 172, 0)
        }
        holder.bindUser(data[position], currUser)
    }

    override fun getItemCount() = data.size

}