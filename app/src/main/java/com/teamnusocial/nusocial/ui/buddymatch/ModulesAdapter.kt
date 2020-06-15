package com.teamnusocial.nusocial.ui.buddymatch

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.model.Module

class ModulesAdapter(var mData: Array<Module>, var context: Context?) : RecyclerView.Adapter<ModulesAdapter.TextHolder>() {
    class TextHolder(val textView: LinearLayout): RecyclerView.ViewHolder(textView)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModulesAdapter.TextHolder {
        val view = LayoutInflater.from(context!!).inflate(R.layout.matched_module_child, parent, false) as LinearLayout
        return TextHolder(view)
    }

    override fun onBindViewHolder(holder: ModulesAdapter.TextHolder, position: Int) {
        holder.textView.findViewById<TextView>(R.id.module_name).text = mData[position].moduleCode
    }

    override fun getItemCount() = mData.size


}