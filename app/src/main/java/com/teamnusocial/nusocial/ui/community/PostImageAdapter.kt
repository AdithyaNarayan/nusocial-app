package com.teamnusocial.nusocial.ui.community

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.teamnusocial.nusocial.R
import java.lang.Exception

class PostImageAdapter(val allImage: List<String>): RecyclerView.Adapter<PostImageAdapter.ImageHolder>() {
    class ImageHolder(val imageView: ConstraintLayout): RecyclerView.ViewHolder(imageView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val imageView = LayoutInflater.from(parent.context).inflate(R.layout.post_image_item, parent, false) as ConstraintLayout
        return ImageHolder(
            imageView
        )
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        try {
            Picasso.get().load(allImage[position]).fit().centerCrop()
                .into(holder.imageView.findViewById(R.id.post_image_item) as ImageView)
        } catch (e : Exception) {
            Log.d("ERROR_DISPLAY", e.message.toString())
        }
    }

    override fun getItemCount() = allImage.size
}