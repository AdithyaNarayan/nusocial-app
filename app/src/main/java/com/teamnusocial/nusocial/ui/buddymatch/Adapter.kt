package com.teamnusocial.nusocial.ui.buddymatch

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.teamnusocial.nusocial.R

class Adapter(private val urls: ArrayList<String>) : RecyclerView.Adapter<Adapter.ImageHolder>() {
    class ImageHolder(val layoutView: ConstraintLayout): RecyclerView.ViewHolder(layoutView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val layoutView = LayoutInflater.from(parent.context).inflate(R.layout.buddymatch_img, parent, false) as ConstraintLayout
        return ImageHolder(layoutView)
    }
    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        Picasso.get().load(urls[position]).into(holder.layoutView.findViewById(R.id.buddy_img) as ImageView)
    }

    override fun getItemCount() = urls.size

}