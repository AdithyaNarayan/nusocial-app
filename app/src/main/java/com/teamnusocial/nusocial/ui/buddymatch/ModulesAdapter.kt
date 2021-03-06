package com.teamnusocial.nusocial.ui.buddymatch

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.model.Module
import soup.neumorphism.NeumorphButton

class ModulesAdapter(var mData: MutableList<Module>, var context: Context?) :
    RecyclerView.Adapter<ModulesAdapter.TextHolder>() {

    private lateinit var clickListener: ModulesAdapter.ItemClickListener

    inner class TextHolder internal constructor(val textView: ConstraintLayout) :
        RecyclerView.ViewHolder(textView), View.OnClickListener {
        override fun onClick(view: View?) {
            clickListener.onItemClick(view, adapterPosition)
        }

        init {
            itemView.setOnClickListener(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModulesAdapter.TextHolder {
        val view = LayoutInflater.from(context!!)
            .inflate(R.layout.matched_module_child, parent, false) as ConstraintLayout
        return TextHolder(view)
    }
    fun onTextClick() {
        var toast = Toast.makeText(context, "Go to your personal page to access the community", Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.BOTTOM or Gravity.CENTER, 0, 0)
        toast.show()
    }
    override fun onBindViewHolder(holder: ModulesAdapter.TextHolder, position: Int) {
        holder.textView.findViewById<TextView>(R.id.module_name).text =
            mData[position].moduleCode

    }

    override fun getItemCount() = mData.size

    fun setClickListener(itemClickListener: ItemClickListener) {
        clickListener = itemClickListener
    }

    interface ItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }


}