package com.teamnusocial.nusocial.ui.messages

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.model.User
import jp.wasabeef.picasso.transformations.MaskTransformation
import soup.neumorphism.NeumorphCardView
import soup.neumorphism.ShapeType

class UserCheckableViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
    View.OnClickListener {

    var username: TextView = itemView.findViewById(R.id.userNameTextView)
    var userCourse: TextView = itemView.findViewById(R.id.userCourseTextView)
    var profileImageView: ImageView = itemView.findViewById(R.id.userImageView)
    var userCard: NeumorphCardView = itemView.findViewById(R.id.userCardCheckable)

    lateinit var user: User

    fun bindUser(user: User) {
        itemView.setOnClickListener(this)
        this.user = user
        username.text = user.name
        userCourse.text = user.courseOfStudy
        Picasso.get().load(user.profilePicturePath).resize(48, 48)
            .transform(
                MaskTransformation(
                    itemView.context,
                    R.drawable.round_rect_transformation
                )
            )
            .into(profileImageView)
    }

    override fun onClick(v: View?) {
        if (userCard.getShapeType() == ShapeType.FLAT) {
            userCard.setShapeType(ShapeType.PRESSED)
        } else {
            userCard.setShapeType(ShapeType.FLAT)
        }
    }

}