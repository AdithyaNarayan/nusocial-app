package com.teamnusocial.nusocial.ui.buddymatch

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.model.User

class CommonFriendAdapter(var mData: MutableList<User>, var context: Context?) : RecyclerView.Adapter<CommonFriendAdapter.ImageHolder>() {

    private lateinit var clickListener: CommonFriendAdapter.ItemClickListener
    inner class ImageHolder internal constructor(val imageView: ImageView): RecyclerView.ViewHolder(imageView), View.OnClickListener {
        override fun onClick(view: View?) {
            clickListener.onItemClick(view, adapterPosition)
        }

        init {
            itemView.setOnClickListener(this)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonFriendAdapter.ImageHolder {
        val view = LayoutInflater.from(context!!).inflate(R.layout.common_friend_item, parent, false) as ImageView
        return ImageHolder(view)
    }

    override fun onBindViewHolder(holder: CommonFriendAdapter.ImageHolder, position: Int) {
        var imageView = holder.imageView.findViewById<ImageView>(R.id.common_friend_avatar)
        Picasso.get().load(mData[position].profilePicturePath).fit().into(imageView)
    }

    override fun getItemCount() = mData.size

    fun setClickListener(itemClickListener: ItemClickListener) {
        clickListener = itemClickListener
    }

    interface ItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }


}