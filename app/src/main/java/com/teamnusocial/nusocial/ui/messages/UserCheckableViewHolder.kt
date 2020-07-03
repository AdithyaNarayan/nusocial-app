package com.teamnusocial.nusocial.ui.messages

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.model.User
import com.teamnusocial.nusocial.ui.buddymatch.BuddyProfileActivity
import kotlinx.android.synthetic.main.fragment_buddymatch.*

class UserCheckableViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    var username: TextView = itemView.findViewById(R.id.userNameTextView)
    var userCourse: TextView = itemView.findViewById(R.id.userCourseTextView)
    var profileImageView: ImageView = itemView.findViewById(R.id.userImageView)
    lateinit var user: User

    fun bindUser(user: User) {
        itemView.setOnClickListener(this)
        this.user = user
        username.text = user.name
        userCourse.text = user.courseOfStudy
        Picasso.get().load(user.profilePicturePath)
            .into(profileImageView)
    }

    override fun onClick(v: View?) {
        val intent = Intent(itemView.context, BuddyProfileActivity::class.java)
        intent.putExtra("USER_IMG", user.profilePicturePath)
        intent.putExtra("USER_NAME", user.name)
        intent.putExtra("USER_ABOUT", user.about)
        itemView.context.startActivity(intent)
        Log.d("LOL","n00b")
    }

}