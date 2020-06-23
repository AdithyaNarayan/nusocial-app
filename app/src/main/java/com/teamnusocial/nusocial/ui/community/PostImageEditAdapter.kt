package com.teamnusocial.nusocial.ui.community

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.teamnusocial.nusocial.R

class PostImageEditAdapter(var allImage: MutableList<String>): RecyclerView.Adapter<PostImageEditAdapter.ImageHolder>() {
    class ImageHolder(val imageView: ConstraintLayout): RecyclerView.ViewHolder(imageView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val imageView = LayoutInflater.from(parent.context).inflate(R.layout.post_image_edit_item, parent, false) as ConstraintLayout
        return ImageHolder(
            imageView
        )
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        val imageView = holder.imageView.findViewById(R.id.post_image__edit_item) as ImageView
        if(imageView != null) Picasso.get().load(allImage[position]).into(imageView)
        holder.imageView.findViewById<Button>(R.id.remove_image).setOnClickListener {
            removeAt(position)
        }
    }
    fun removeAt(position: Int) {
        allImage.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, allImage.size)
    }

    override fun getItemCount() = allImage.size
}