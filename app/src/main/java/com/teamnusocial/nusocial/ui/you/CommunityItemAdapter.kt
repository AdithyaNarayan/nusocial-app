package com.teamnusocial.nusocial.ui.you

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.model.Community

class CommunityItemAdapter(var mData: MutableList<Community>, var context: Context?) : RecyclerView.Adapter<CommunityItemAdapter.TextHolder>() {
    private lateinit var clickListener: CommunityItemAdapter.ItemClickListener
    inner class TextHolder internal constructor(val textView: LinearLayout): RecyclerView.ViewHolder(textView), View.OnClickListener {
        override fun onClick(view: View?) {
            clickListener.onItemClick(view, adapterPosition)
        }

        init {
            itemView.setOnClickListener(this)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommunityItemAdapter.TextHolder {
        val view = LayoutInflater.from(context!!).inflate(R.layout.community_item, parent, false) as LinearLayout
        return TextHolder(view)
    }

    override fun onBindViewHolder(holder: CommunityItemAdapter.TextHolder, position: Int) {
        holder.textView.findViewById<TextView>(R.id.comm_name).text = mData[position].name
    }

    override fun getItemCount() = mData.size

    fun setClickListener(itemClickListener: ItemClickListener) {
        clickListener = itemClickListener
    }

    interface ItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }
}