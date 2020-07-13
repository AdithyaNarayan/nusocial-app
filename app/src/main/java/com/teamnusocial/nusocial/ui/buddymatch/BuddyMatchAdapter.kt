package com.teamnusocial.nusocial.ui.buddymatch

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.teamnusocial.nusocial.R

class BuddyMatchAdapter(private val urls: ArrayList<String>) : RecyclerView.Adapter<BuddyMatchAdapter.ImageHolder>() {
    class ImageHolder(val layoutView: ConstraintLayout): RecyclerView.ViewHolder(layoutView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val layoutView = LayoutInflater.from(parent.context).inflate(R.layout.item_buddymatch, parent, false) as ConstraintLayout
        return ImageHolder(layoutView)
    }
    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        Picasso.get().load(urls[position]).into(holder.layoutView.findViewById(R.id.buddy_img) as ImageView)
    }

    override fun getItemCount() = urls.size

}