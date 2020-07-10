package com.teamnusocial.nusocial.ui.messages

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.model.User

class UserCheckableAdapter(val data: List<User>): RecyclerView.Adapter<UserCheckableViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserCheckableViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.card_user_checkable, parent, false)
        return UserCheckableViewHolder(layout)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: UserCheckableViewHolder, position: Int) {
        holder.bindUser(data[position])
    }

}