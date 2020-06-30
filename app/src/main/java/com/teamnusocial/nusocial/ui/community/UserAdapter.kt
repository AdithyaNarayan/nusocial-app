package com.teamnusocial.nusocial.ui.community

import android.content.ContentProvider
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.model.User

class UserAdapter(val data: List<User>): RecyclerView.Adapter<UserViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.card_user, parent, false)
        return UserViewHolder(layout)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bindUser(data[position])
    }

}