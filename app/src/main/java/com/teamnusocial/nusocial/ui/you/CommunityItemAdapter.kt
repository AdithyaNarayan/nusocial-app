package com.teamnusocial.nusocial.ui.you

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.model.Community
import com.teamnusocial.nusocial.data.model.User
import com.teamnusocial.nusocial.ui.community.SingleCommunityActivity

class CommunityItemAdapter(var mData: MutableList<Community>, var context: Context?, val you: User) : RecyclerView.Adapter<CommunityItemAdapter.TextHolder>() {
    private lateinit var clickListener: CommunityItemAdapter.ItemClickListener
    inner class TextHolder internal constructor(val textView: LinearLayout): RecyclerView.ViewHolder(textView), View.OnClickListener {
        override fun onClick(view: View?) {
            Log.d("TEST_COMM", "yes here 1")
            clickListener.onItemClick(view, adapterPosition)
        }

        init {
            itemView.setOnClickListener(this)
        }
    }
    fun goToComm(position: Int) {
        val intent = Intent(context, SingleCommunityActivity::class.java)
        //intent.putExtra("COMM_TIME", getTimeToDelete(viewModel.otherCommunities[position].id))
        Log.d("TEST_COMM", "yes here")
        intent.putExtra("COMMUNITY_DATA", mData[position])
        intent.putExtra("USER_DATA", you)
        context!!.startActivity(intent)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommunityItemAdapter.TextHolder {
        val view = LayoutInflater.from(context!!).inflate(R.layout.community_item, parent, false) as LinearLayout
        return TextHolder(view)
    }

    override fun onBindViewHolder(holder: CommunityItemAdapter.TextHolder, position: Int) {
        holder.textView.findViewById<TextView>(R.id.comm_name).text = mData[position].name
        Picasso.get().load(mData[position].coverImageUrl).into(holder.textView.findViewById<ImageView>(R.id.comm_item_img))
        holder.textView.findViewById<ImageView>(R.id.comm_item_img).setOnClickListener {
            //Log.d("TEST_COMM", "yes here 2")
            if(you.uid != "")
                goToComm(position)
        }
        holder.textView.findViewById<TextView>(R.id.comm_name).setOnClickListener {
            //Log.d("TEST_COMM", "yes here 3")
            if(you.uid != "")
                goToComm(position)
        }

    }

    override fun getItemCount() = mData.size

    fun setClickListener(itemClickListener: ItemClickListener) {
        clickListener = itemClickListener
    }

    interface ItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }
}