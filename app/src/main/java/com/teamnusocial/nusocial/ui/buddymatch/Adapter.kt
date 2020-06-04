package com.teamnusocial.nusocial.ui.buddymatch

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.teamnusocial.nusocial.R

class Adapter(private val urls: ArrayList<String>) : RecyclerView.Adapter<Adapter.ImageHolder>() {
    class ImageHolder(val imageView: ImageView): RecyclerView.ViewHolder(imageView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val imageView = LayoutInflater.from(parent.context).inflate(R.layout.buddymatch_img, parent, false) as ImageView
        return ImageHolder(imageView)
    }
    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        Picasso.get().load(urls[position % urls.size]).into(holder.imageView)
    }

    override fun getItemCount() = Int.MAX_VALUE
}