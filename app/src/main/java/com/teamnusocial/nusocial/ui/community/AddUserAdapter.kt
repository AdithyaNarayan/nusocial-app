package com.teamnusocial.nusocial.ui.community

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.model.User
import soup.neumorphism.NeumorphCardView

class AddUserAdapter(val context: Context, val users: MutableList<User>, var userList: MutableList<User>) : RecyclerView.Adapter<AddUserAdapter.ViewHolder>() {
    //private lateinit var clickListener: AddUserAdapter.ItemClickListener
    inner class ViewHolder internal constructor(val view: ConstraintLayout): RecyclerView.ViewHolder(view)/*, View.OnClickListener {
            override fun onClick(view: View?) {
                clickListener.onItemClick(view, adapterPosition)
            }
            init {
                itemView.setOnClickListener(this)
            }
    }*/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false) as ConstraintLayout
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val profile_pic = holder.view.findViewById<ImageView>(R.id.user_item_profile_pic)
        val name = holder.view.findViewById<TextView>(R.id.user_item_name)
        val check_box = holder.view.findViewById<CheckBox>(R.id.checkbox_wrapper)
        val neu_card = holder.view.findViewById<NeumorphCardView>(R.id.card_wrapper)

        Picasso.get().load(users[position].profilePicturePath).into(profile_pic)
        name.text = users[position].name
        check_box.setOnCheckedChangeListener{button,isChecked ->
            if(isChecked) {
                name.setTextColor(context.resources.getColor(R.color.white))
                //wrapper.setBackgroundResource(R.drawable.background_home)
                neu_card.setBackgroundColor(context.resources.getColor(R.color.black))
                userList.add(users[position])
            } else {
                name.setTextColor(context.resources.getColor(R.color.black))
                //wrapper.setBackgroundResource(R.drawable.background)
                neu_card.setBackgroundColor(context.resources.getColor(R.color.white))
                userList.remove(users[position])
            }
        }
    }

    override fun getItemCount() = users.size
    /*fun setClickListener(itemClickListener: ItemClickListener) {
        clickListener = itemClickListener
    }

    interface ItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }*/
}