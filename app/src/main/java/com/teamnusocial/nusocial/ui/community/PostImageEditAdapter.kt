package com.teamnusocial.nusocial.ui.community

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.teamnusocial.nusocial.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PostImageEditAdapter(var allImage: MutableList<String>, val isCreate: Boolean, val postID: String): RecyclerView.Adapter<PostImageEditAdapter.ImageHolder>() {
    class ImageHolder(val imageView: ConstraintLayout): RecyclerView.ViewHolder(imageView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val imageView = LayoutInflater.from(parent.context).inflate(R.layout.post_image_edit_item, parent, false) as ConstraintLayout
        return ImageHolder(
            imageView
        )
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        val imageView = holder.imageView.findViewById(R.id.post_image__edit_item) as ImageView
        if(imageView != null) Picasso.get().load(allImage[position]).fit().centerCrop().into(imageView)
        holder.imageView.findViewById<Button>(R.id.remove_image).setOnClickListener {
            removeAt(position)
        }
    }
    fun removeAt(position: Int) {
        val imagePath = allImage[position]
        allImage.removeAt(position)
        var startIndex = 0
        var endIndex = 0
        for(index in 0 until imagePath.length) {
            if(imagePath[index] == '%') startIndex = index + 3
            else if(imagePath[index] == '?') {
                endIndex = index
                break
            }
        }
        Log.d("TEST_IMG_PATH", "HERE is ${imagePath}")
        if(!isCreate) {
            CoroutineScope(Dispatchers.IO).launch {
                FirebaseStorage.getInstance().getReference("/post_images/${imagePath.substring(startIndex, endIndex)}").delete()
            }
        }
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, allImage.size)
    }

    override fun getItemCount() = allImage.size
}